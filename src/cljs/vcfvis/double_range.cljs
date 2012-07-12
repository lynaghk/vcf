;;Double range slider
(ns vcfvis.double-range
  (:use-macros [c2.util :only [pp p bind!]])
  (:require [c2.dom :as dom]
            [goog.events :as gevents]
            [goog.fx.Dragger :as goog.fx.Dragger]
            [goog.ui.TwoThumbSlider :as goog.ui.TwoThumbSlider]))

(defn init! [el callback]
  (let [$tt (dom/->dom el)
        tt (new goog.ui.TwoThumbSlider)]
    (doto tt
      (.decorate $tt)
      (gevents/listen goog.ui.Component.EventType.CHANGE
                      #(let [v (.getValue tt)]
                         (callback [v (+ v (.getExtent tt))]))))))
