(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]]
               [clojure.core.match.js :only [match]])
  (:use [c2.core :only [unify]]
        [c2.maths :only [irange]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [c2.ticks :as ticks]))

(def margin 40)

;;width and height of data frame
(def height 400)
(def width 900)

(bind! "#histogram"
       (let [x (scale/linear :domain [0 100]
                             :range [0 width])]

         [:svg#histogram {:height (+ height (* 2 margin))
                          :width  (+ width (* 2 margin))}
          [:g {:transform (svg/translate [margin margin])}
           [:g.data-frame]
           [:g.axis.ordinate]
           [:g.axis.abscissa {:transform (svg/translate [0 height])}
            (svg/axis x (irange 0 100 10)
                      :orientation :bottom)]]]))
