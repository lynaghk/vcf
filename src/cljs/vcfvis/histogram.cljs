(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]]
               [dubstep.macros :only [publish! subscribe!]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange extent]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.ui :as ui]
            [vcfvis.brush :as brush]
            [c2.dom :as dom]
            [singult.core :as singult]
            [c2.event :as event]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [goog.string :as gstr]))

(def height ui/hist-height)
(def width ui/hist-width)
(def margin ui/hist-margin)
(def inter-hist-margin ui/inter-hist-margin)
(def axis-height ui/axis-height)

;; ;;Whenever the range sliders move, we're looking at a new subset of the data, so reset the buttons
;; (add-watch !selected-extent :reset-analysis-status-buttons
;;            (fn [_ _ _ _] (data/reset-statuses!)))


(defn hist-svg* [vcf metric & {:keys [margin height width bars?]
                               :or {bars? true}}]
  (let [{metric-id :id scale-x :scale-x} metric
        {:keys [dimension binned bin-width]} (get-in vcf [:cf metric-id])
        ;;Since we're only interested in relative density, histograms have free y-scales.
        data-extent [0 (aget (first (.top binned 1)) "value")]
        no-data? (zero? (apply - data-extent))
        scale-y (scale/linear :domain data-extent
                              :range [0 height])
        scale-x (assoc-in scale-x [:range 1] width)
        dx (- (scale-x bin-width) (scale-x 0))]
    
    [:svg {:width (+ width (* 2 margin)) :height (+ height inter-hist-margin)}
     [:g {:transform (svg/translate [margin margin])}

      [:text.message {:x (/ width 2) :y (/ height 2)}
       (when no-data?
         "No available data; try clearing filters on other dimensions.")]
      
      [:g.data-frame {:transform (str (svg/translate [0 height])
                                      (svg/scale [1 -1]))}
       [:g.distribution
        (when-not no-data?
          (if bars?
            (for [d (.all binned)]
              (let [x (aget d "key"), count (aget d "value")]
                [:rect.bar {:x (scale-x x)
                            :width dx
                            :height (scale-y count)}]))
            ;;else, render using a path element
            [:path
             ;; ;;Path Bars
             ;; (str "M" (scale-x x) ",0"
             ;;      "l0," h
             ;;      "l" dx "," 0
             ;;      "l" 0 "," (- h))
             {:d (str "M"
                      (.join (.map (.all binned)
                                   (fn [d]
                                     (let [x (aget d "key"), count (aget d "value")
                                           h (scale-y count)]
                                       (str (scale-x x) "," h))))
                             "L"))}]))]]]]))




(defn draw-mini-hist-for-metric! [m]
  (let [vcf (first @core/!vcfs) ;;TODO, faceting
        mini-width (js/parseFloat (dom/style "#metrics" :width))
        mini-height 100]
    (singult/merge! (dom/select (str "#metric-" (:id m) " .mini-hist"))
                    [:div.mini-hist (hist-svg* vcf m
                                               :margin 0
                                               :height mini-height
                                               :width mini-width
                                               :bars? false)])))

(defn draw-histogram! [vcfs metric]
  (let [{x :scale-x} metric]
    (singult/merge! (dom/select "#main-hist")
                    [:div#main-hist
                     [:div#histograms
                      (for [vcf vcfs]
                        [:div.histogram
                         [:span.label (vcf :file-url)]
                         (hist-svg* vcf metric
                                    :margin margin
                                    :height (- height axis-height)
                                    :width width)])]

                     [:div#hist-axis
                      [:div.axis.abscissa
                       [:svg {:width (+ width (* 2 margin)) :height axis-height}
                        [:g {:transform (svg/translate [margin 2])}
                         (svg/axis x (:ticks x)
                                   :orientation :bottom
                                   :formatter (partial gstr/format "%.1f"))]]]]])

    (let [!b (brush/init! "#histograms .histogram svg .data-frame"
                          x (scale/linear :range [0 height]))]

      (add-watch !b :onbrush (fn [_ _ _ [xs _]]
                               (publish! {:metric-brushed metric :extent xs}))))))

(defn clear-histogram! []
  (singult/merge! (dom/select "#main-hist")
                  [:div#main-hist
                   [:div#histograms]
                   [:div#hist-axis]])
  (publish! {:count-updated nil}))

(constrain!
 (let [vcfs @core/!vcfs
       metric @core/!metric]
   (if (seq vcfs)
     (draw-histogram! vcfs metric)
     ;;If no VCFs, clear everything
     (clear-histogram!))))

