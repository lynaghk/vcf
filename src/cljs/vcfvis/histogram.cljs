(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange extent]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.ui :as ui]
            [vcfvis.double-range :as double-range]
            [c2.dom :as dom]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [goog.string :as gstr]))

(def height ui/hist-height)
(def width ui/hist-width)
(def margin ui/hist-margin)
(def inter-hist-margin ui/inter-hist-margin)
(def axis-height ui/axis-height)



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


(defn hist-svg* [vcf metric & {:keys [margin]}]
  (let [height (- height axis-height)
        {metric-id :id scale-x :scale-x} metric
        {:keys [dimension binned bin-width]} (get-in vcf [:cf metric-id])
        ;;Since we're only interested in relative density, histograms have free y-scales.
        scale-y (scale/linear :domain [0 (aget (first (.top binned 1)) "value")]
                              :range [0 height])
        dx (- (scale-x bin-width) (scale-x 0))]

    [:svg {:width (+ width (* 2 margin)) :height (+ height inter-hist-margin)}
     [:g {:transform (svg/translate [margin margin])}
      [:g.distribution {:transform (str (svg/translate [0 height])
                                        (svg/scale [1 -1]))}

       (for [d (.all binned)]
         (let [x (aget d "key"), count (aget d "value")]
           [:rect.bar {:x (scale-x x)
                       :width (- (scale-x (+ x bin-width))
                                 (scale-x x))
                       :height (scale-y count)}]))]]]))




(bind! "#main-hist"
       (let [vcfs @core/!vcfs]
         (if (seq vcfs)
           (let [{x :scale-x :as metric} @core/!metric]
             [:div#main-hist
              [:div#histograms
               (for [vcf vcfs]
                 [:div.histogram
                  [:span.label (vcf :file-url)]
                  (hist-svg* vcf metric :margin margin)])]

              [:div#hist-axis
               [:div.axis.abscissa
                [:svg {:width (+ width (* 2 margin)) :height axis-height}
                 [:g {:transform (svg/translate [margin 2])}
                  (svg/axis x (:ticks x)
                            :orientation :bottom
                            :formatter (partial gstr/format "%.1f"))]]]]])


           ;;If no VCFs, clear everything
           [:div#main-hist
            [:div#histograms]
            [:div#hist-axis]])))
