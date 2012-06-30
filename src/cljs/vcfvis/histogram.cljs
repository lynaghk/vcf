(ns vcfvis.histogram
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [constrain!]]
               [clojure.core.match.js :only [match]])
  (:use [c2.core :only [unify]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [c2.ticks :as ticks]))

(def margin 40)

;;width and height of data frame
(def height 400)
(def width 900)



(defn ->xy
  "Convert coordinates (potentially map of `{:x :y}`) to 2-vector."
  [coordinates]
  (match [coordinates]
         [{:x x :y y}] [x y]
         [[x y]] [x y]))

#_(bind! "#histogram"
       (let [x (scale/linear :domain [0 100]
                             :range [0 width])]
         (pp (x 5))

         [:svg#histogram {:height (+ height (* 2 margin))
                          :width  (+ width (* 2 margin))}
          [:g {:transform (svg/translate [margin margin])}
           [:g.data-frame]
           [:g.axis.ordinate]
           [:g.axis.abscissa {:transform (svg/translate [0 height])}
            (pp (svg/axis x (range 0 100 10)))]]]))
