(ns brush.test
  (:use-macros [c2.util :only [p pp bind!]]
               [reflex.macros :only [constrain!]])
  (:require [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [goog.fx.Dragger :as goog.fx.Dragger]
            [c2.event :as event]
            [goog.events :as gevents]
            [clojure.string :as str]))

(def width 500)
(def height 300)
(def margin 20)
(def n 200)
(def data (repeatedly n rand))

(def scale-x (scale/linear :domain [0 (dec n)] :range [0 width]))
(def scale-y (scale/linear :range [0 height]))


(dom/append! "#brush"
             [:svg {:width (+ width (* 2 margin)) :height (+ 50 height)}
              [:g {:transform (svg/translate [margin 0])}
               [:g.distribution {:transform (str (svg/translate [0 height])
                                                 (svg/scale [1 -1]))}

                [:path
                 {:d (str "M"
                          (str/join "L" (map-indexed (fn [idx d]
                                                       (str (scale-x idx) "," (scale-y d)))
                                                     data)))}]
                ]]])





(defn set-drag-limits! [dragger [x y w h]]
  (.setLimits dragger
              (goog.math.Rect. x y w h)))

;;;;;;;;;;;;;;;
;;Dragger subclass that doesn't actually move its element; used just for picking up events

(defn SVGDrag [el]
  (.call goog.fx.Dragger (js* "this") el))
(goog.inherits SVGDrag goog.fx.Dragger)
(set! (.-defaultAction (.-prototype SVGDrag))
      (fn [x y]
        (pp [x y])
        #_(dom/attr (.-target (js* "this")) {:x x :y y})))





;;TODO generalize to support y-only brushing and full rectangular brushing.
(defn brush!
  "Given scale and element to append to, creates an SVG brush overlay and returns an atom that references the brush extent."
  [el scale-x scale-y]
  (let [width (let [[xmin xmax] (:range scale-x)]
                (- xmax xmin))
        height (let [[ymin ymax] (:range scale-y)]
                 (- ymax ymin))
        ;;extent of selection, in data-space
        !extent (atom [[0 100] [0 0]])
        $brush (dom/append! el [:g.brush])]

    (bind! $brush
           (let [[[x1 x2] [y1 y2]] @!extent]
             [:g.brush
              [:rect.background {:x 0 :width width :height height}]
              [:rect.extent {:x (scale-x x1) :width (- (scale-x x2)
                                                       (scale-x x1))
                             :height height}]
              [:g.resize.e
               [:rect {:x (scale-x x1)
                       :width 5 :height height}]]
              [:g.resize.w
               [:rect {:x (scale-x x2)
                       :width 5 :height height}]]]))
    
    ;;Add event handlers
    (let [dragger (SVGDrag. (dom/select ".extent" $brush))]
      (gevents/listen dragger goog.fx.Dragger.EventType.DRAG
                          (fn [e]
                            (p e)))

      (set-drag-limits! dragger [0 0 width 0]))



    ;;return extent atom.
    !extent))

(let [!b (brush! "#brush g.distribution " scale-x scale-y)]
  (constrain!
   (pp @!b)))






;; function resizePath(d) {
;;   var e = +(d == "e"),
;;       x = e ? 1 : -1,
;;       y = height / 3;
;;   return "M" + (.5 * x) + "," + y
;;       + "A6,6 0 0 " + e + " " + (6.5 * x) + "," + (y + 6)
;;       + "V" + (2 * y - 6)
;;       + "A6,6 0 0 " + e + " " + (.5 * x) + "," + (2 * y)
;;       + "Z"
;;       + "M" + (2.5 * x) + "," + (y + 8)
;;       + "V" + (2 * y - 8)
;;       + "M" + (4.5 * x) + "," + (y + 8)
;;       + "V" + (2 * y - 8);
;; }
