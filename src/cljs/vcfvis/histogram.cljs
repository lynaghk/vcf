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
       (when @!extent
         (let [{:keys [extent ticks]} (ticks/search @!extent :length width)
               x (scale/linear :domain extent
                               :range [0 width])]

           [:svg#histogram {:height (+ height (* 2 margin))
                            :width  (+ width (* 2 margin))}
            [:g {:transform (svg/translate [margin margin])}
             [:g.data-frame]
             [:g.axis.ordinate]
             [:g.axis.abscissa {:transform (svg/translate [0 height])}
              (svg/axis x ticks :orientation :bottom)]]])))
