(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]])
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
(def axis-height (js/parseFloat (dom/style "#hist-axis" :height)))

(def height
  "height available to histogram facet grid"
  (js/parseFloat (dom/style "#histograms" :height)))

(def width
  "Width of histogram facet grid"
  (- (js/parseFloat (dom/style "#histograms" :width))
     (* 2 margin)))


;; (def !selected-extent (atom [0 1]))
;; ;;Range selector
;; (let [$tt (-> (dom/select "#range-selector")
;;               (dom/style :width width))
;;       tt (double-range/init! $tt #(reset! !selected-extent %))]

;;   (constrain!
;;    (dom/style $tt :visibility
;;               (if (seq @core/!vcfs) "visible" "hidden")))

;;   ;;possible todo: use pubsub bus rather than side-effecting fn.
;;   (defn update-range-selector! [[min max] bin-width]
;;     (doto tt
;;       (.setMinimum min) (.setMaximum max)
;;       (.setStep bin-width) (.setMinExtent bin-width) (.setBlockIncrement bin-width)
;;       (.setValueAndExtent min (- max min)))))

;; (constrain!
;;  (let [{:keys [bin-width range]} @core/!metric]
;;    (update-range-selector! range bin-width)))

;; ;;Whenever the range sliders move, we're looking at a new subset of the data, so reset the buttons
;; (add-watch !selected-extent :reset-analysis-status-buttons
;;            (fn [_ _ _ _] (data/reset-statuses!)))




(defn histogram* [vcf scale-x metric]
  (let [height (- height axis-height)
        {:keys [dimension binned bin-width]} (get-in vcf [:cf (metric :id)])
        scale-y (scale/linear :domain [0 (aget (first (.top binned 1)) "value")]
                              :range [0 height])
        dx (- (scale-x bin-width) (scale-x 0))]

    [:div.histogram
     [:span.label (vcf :file-url)]

     [:svg {:width (+ width (* 2 margin)) :height (+ height inter-hist-margin)}
      [:g {:transform (svg/translate [margin margin])}
       [:g.distribution {:transform (str (svg/translate [0 height])
                                         (svg/scale [1 -1]))}
        (for [d (.all binned)]
          (let [x (aget d "key"), count (aget d "value")]
            [:rect.bar {:x (scale-x x)
                        :width (- (scale-x (+ x bin-width))
                                  (scale-x x))
                        :height (scale-y count)}]))]]]]))




(bind! "#main-hist"
       (let [vcfs @core/!vcfs]
         (if (seq vcfs)
           (let [metric @core/!metric
                 metric-extent (metric :range)
                 {:keys [ticks]} (ticks/search metric-extent
                                               :clamp? true :length width)
                 x (scale/linear :domain metric-extent
                                 :range [0 width])]
             [:div#main-hist
              [:div#histograms

               ;;histogram distributions
               (histogram* (first vcfs) x metric)]

              [:div#hist-axis
               [:div.axis.abscissa
                [:svg {:width (+ width (* 2 margin)) :height axis-height}
                 [:g {:transform (svg/translate [margin 2])}
                  (svg/axis x ticks :orientation :bottom
                            :formatter (partial gstr/format "%.1f"))]]]]])
           ;;If no VCFs, clear everything
           [:div#main-hist
            [:div#histograms]
            [:div#hist-axis]])))
