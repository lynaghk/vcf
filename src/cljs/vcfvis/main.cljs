(ns vcfvis.main
  (:use-macros [c2.util :only [pp p timeout]]
               [reflex.macros :only [constrain!]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.controls :as controls]))

(add-watch controls/file-selector :load-metrics
           (fn [files]
             (data/load-metrics files
              (partial reset! core/!vcfs))))

;;Request file list from server
(data/available-files (partial reset! core/!available-files))
