(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [computed-observable constrain!]]
               [clojure.core.match.js :only [match]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange extent]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.double-range :as double-range]
            [c2.dom :as dom]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [c2.ticks :as ticks]
            [goog.string :as gstr]))

(def margin "left/right margin" 20)
(def inter-hist-margin "vertical margin between stacked histograms" 20)
(def axis-height 20)

(def height
  "height available to histogram facet grid"
  (js/parseFloat (dom/style "#histograms" :height)))

(def width
  "width of histogram facet grid"
  900)


(def !selected-extent (atom [0 1]))
;;Range selector
(let [$tt (-> (dom/select "#range-selector")
              (dom/style :width width))
      tt (double-range/init! $tt #(reset! !selected-extent %))]

  (constrain!
   (dom/style $tt :visibility
              (if (seq @core/!vcfs) "visible" "hidden")))

  ;;possible todo: use pubsub bus rather than side-effecting fn.
  (defn update-range-selector! [[min max] bin-width]
    (doto tt
      (.setMinimum min) (.setMaximum max)
      (.setStep bin-width) (.setMinExtent bin-width) (.setBlockIncrement bin-width)
      (.setValueAndExtent min (- max min)))))

(constrain!
 (let [{:keys [bin-width range]} @core/!metric]
   (update-range-selector! range bin-width)))

;;Whenever the range sliders move, we're looking at a new subset of the data, so reset the buttons
(add-watch !selected-extent :reset-analysis-status-buttons
           (fn [_ _ _ _] (data/reset-statuses!)))


(defn histograms* [vcfs x-scale]
  (let [metric-id (@core/!metric :id)
        metrics (map #(core/vcf-metric % metric-id) vcfs)
        max-val (apply max (flatten (map :vals metrics)))
        height (- (/ (- height axis-height) (count vcfs))
                  inter-hist-margin)
        y (scale/linear :domain [0 max-val]
                        :range [0 height])
        ;;The bin width is the same for a given metric across all samples
        bin-width (:bin-width (core/vcf-metric (first vcfs) metric-id))
        dx (- (x-scale bin-width) (x-scale 0))]

    [:div.span12
     (unify vcfs
            (fn [vcf]
              [:div.histogram
               [:span.label (vcf :filename)]
               (case (get @data/!analysis-status (vcf :filename))
                 :completed  [:button.btn {:properties {:disabled true}} "Completed"]
                 :running    [:button.btn {:properties {:disabled true}} "Running..."]
                 nil         [:button.btn {:properties {:disabled false}} "Export subset"])
               [:svg {:width (+ width (* 2 margin)) :height (+ height inter-hist-margin)}
                [:g {:transform (svg/translate [margin margin])}
                 [:g.distribution {:transform (str (svg/translate [0 height])
                                                   (svg/scale [1 -1]))}
                  (map-indexed (fn [idx v]
                                 [:rect {:x (* dx idx) :width dx
                                         :height (y v)}])
                               ((core/vcf-metric vcf metric-id) :vals))]]]])
            :force-update? true)]))


(bind! "#histograms"
       (let [vcfs @core/!vcfs]
         (if (seq vcfs)
           (let [metric (core/vcf-metric (first vcfs)
                                         (@core/!metric :id))
                 metric-extent (metric :range)
                 {:keys [ticks]} (ticks/search metric-extent
                                               :clamp? true :length width)
                 x (scale/linear :domain metric-extent
                                 :range [0 width])]

             [:div.row#histograms

              ;;histogram distributions
              (histograms* vcfs x)

              ;;x-axis
              [:div.span12
               [:div.axis.abscissa
                [:svg {:width (+ width (* 2 margin)) :height axis-height}
                 [:g {:transform (svg/translate [margin 2])}
                  (svg/axis x ticks :orientation :bottom
                            :formatter (partial gstr/format "%.1f"))]]]]])
           ;;If no VCFs, clear everything
           [:div.row#histograms])))

(event/on "#histograms" "button" :click
          (fn [{:keys [filename]}]
            (let [metric-id (name (@core/!metric :id))]
              (data/filter-analysis
               {:file-url filename
                :metrics {metric-id @!selected-extent}}))))
