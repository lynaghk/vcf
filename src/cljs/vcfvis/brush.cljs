(ns vcfvis.brush
  (:use-macros [c2.util :only [p pp bind!]])
  (:require [c2.dom :as dom]
            [c2.scale :as scale]
            [c2.svg :as svg]
            [goog.fx.Dragger :as goog.fx.Dragger]
            [goog.events :as gevents]))

;;;;;;;;;;;;;;;
;;Dragger subclass that doesn't actually move its element; used just for picking up events
(defn SVGDrag [el]
  (.call goog.fx.Dragger (js* "this") el))
(goog.inherits SVGDrag goog.fx.Dragger)
(set! (.-defaultAction (.-prototype SVGDrag))
      (fn [x y] "Do nothing"))


;;TODO generalize to support y-only brushing and full rectangular brushing.
(defn init!
  "Given scale and element to append to, creates an SVG brush overlay and returns an atom that references the brush extent."
  [el scale-x scale-y]
  (let [width (let [[xmin xmax] (:range scale-x)]
                (- xmax xmin))
        height (let [[ymin ymax] (:range scale-y)]
                 (- ymax ymin))
        ;;extent of selection, in data-space
        !extent (atom [[0 0] [0 0]])
        $brush (dom/append! el [:g.brush])]

    (bind! $brush
           (let [[[x1 x2] [y1 y2]] @!extent]
             [:g.brush {:style {:display (when (zero? (- x1 x2)) "none")}}
              [:rect.background {:x 0 :width width :height height}]
              [:rect.extent {:x (scale-x x1) :width (- (scale-x x2)
                                                       (scale-x x1))
                             :height height}]
              [:g.resize.w
               [:rect {:x (scale-x x1)
                       :width 5 :height height}]]
              [:g.resize.e
               [:rect {:x (scale-x x2)
                       :width 5 :height height}]]]))

    ;;Add event handlers
    (let [!extent-at-start (atom nil)
          [xmin xmax] (:domain scale-x)
          ix (scale/invert scale-x)
          min-extent (ix 10) ;;10px min exent

          start-drag! #(reset! !extent-at-start @!extent)
          drag-fn! (fn [dragger transform-x]
                     (gevents/listen dragger goog.fx.Dragger.EventType.DRAG
                                     (fn [e]
                                       (let [[[x1 x2] ys] @!extent-at-start
                                             w (- x2 x1)
                                             dx (ix (.-left e)) ;;convert from pixel-space to data-space
                                             xs (transform-x dx x1 x2 w)]
                                         (reset! !extent [xs ys])))))

          dragger (SVGDrag. (dom/select ".extent" $brush))
          left (SVGDrag. (dom/select ".resize.w" $brush))
          right (SVGDrag. (dom/select ".resize.e" $brush))]

      (doseq [d [dragger left right]]
        (gevents/listen d goog.fx.Dragger.EventType.START start-drag!))

      (drag-fn! dragger (fn [dx x1 x2 w]
                          (cond
                           ;;moving too far left
                           (< (+ dx x1) xmin) [xmin (+ xmin w)]
                           ;;moving too far right
                           (> (+ dx x2) xmax) [(- xmax w) xmax]
                           :else [(+ dx x1) (+ dx x2)])))
      
      (drag-fn! left (fn [dx x1 x2 w]
                       (cond
                        ;;moving too far left
                        (< (+ dx x1) xmin) [xmin x2]
                        ;;moving beyond the right bar
                        (> (+ dx x1) x2) [(- x2 min-extent) x2]
                        :else [(+ dx x1) x2])))
      
      (drag-fn! right (fn [dx x1 x2 w]
                       (cond
                        ;;moving too far right
                        (> (+ dx x2) xmax) [x1 xmax]
                        ;;moving beyond the left bar 
                        (< (+ dx x2) x1) [x1 (+ x1 min-extent)]
                        :else [x1 (+ dx x2)]))))



    ;;return extent atom.
    !extent))
