(ns vcfvis.ui
  (:use-macros [c2.util :only [pp p bind!]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]
            [aahru.ui]))

(def set-user aahru.ui/set-user)
(def set-navigation aahru.ui/set-navigation)

;;;;;;;;;;;;;;;;;;;
;;Histogram params

(def hist-margin "left/right margin" 20)
(def inter-hist-margin "vertical margin between stacked histograms" 20)
(def axis-height (js/parseFloat (dom/style "#hist-axis" :height)))

(def hist-height
  "Height available to histogram facet grid"
  (js/parseFloat (dom/style "#histograms" :height)))

(def hist-width
  "Width of histogram facet grid"
  (- (js/parseFloat (dom/style "#histograms" :width))
     (* 2 hist-margin)))

(def hist-bins "number of histogram bins" 100)
