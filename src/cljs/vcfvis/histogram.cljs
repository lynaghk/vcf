(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [computed-observable constrain!]]
               [clojure.core.match.js :only [match]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange extent]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [c2.ticks :as ticks]))

(def margin 40)

;;width and height of data frame
(def height 400)
(def width 900)

(def !extent
  "Extent of x-scale for the currently selected metric of the current VCFs."
  (computed-observable
   (let [domains (mapcat #(get-in (core/vcf-metric % (@core/!metric :id))
                                  [:x-scale :domain])
                         @core/!vcfs)]
     (when (seq domains)
       (extent domains)))))

(bind! "#histogram"
       [:svg#histogram {:height (+ height (* 2 margin))
                        :width  (+ width (* 2 margin))}
        
        (let [vcfs @core/!vcfs]
          (when (seq vcfs)
            (let [metric-extent (get-in (core/vcf-metric (first vcfs)
                                                  (@core/!metric :id))
                                 [:x-scale :domain])
                  {:keys [extent ticks]} (ticks/search metric-extent
                                                       :length width)
                  x (scale/linear :domain extent
                                  :range [0 width])]
              [:g {:transform (svg/translate [margin margin])}
               [:g.data-frame {:transform (str (svg/translate [0 height])
                                               (svg/scale [1 -1]))}
                (let [metric-id (@core/!metric :id)
                      metrics (map #(core/vcf-metric % metric-id) vcfs)
                      max-val (apply max (flatten (map :vals metrics)))
                      y (scale/linear :domain [0 max-val]
                                      :range [0 height])
                      bin-width (:bin-width (core/vcf-metric (first vcfs) metric-id))
                      dx (- (x bin-width) (x 0))]

                  (unify metrics
                         (fn [metric]
                           [:g.distribution
                            (map-indexed
                             (fn [idx v]
                               [:rect {:x (* dx idx) :width dx
                                       :height (y v)}])
                             (metric :vals))])))]
               [:g.axis.ordinate]
               [:g.axis.abscissa {:transform (svg/translate [0 height])}
                (svg/axis x ticks :orientation :bottom)
                ]])))])
