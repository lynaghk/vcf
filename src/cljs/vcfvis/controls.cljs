(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [constrain!]])
  (:use [chosen.core :only [ichooseu! options selected]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]))


(def num-datasets 2)

(def selectors
  (let [$selectors (dom/select ".file-selectors")]
    (doall (for [_ (range num-datasets)]
             (let [$sel (dom/append! $selectors [:select])
                   !c (ichooseu! $sel)]
               ;;The selectors should always reflect the user's avaliable flies
               (constrain! (options !c @core/!available-filenames))
               !c)))))


