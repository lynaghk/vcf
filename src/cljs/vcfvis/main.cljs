(ns vcfvis.main
  (:use-macros [c2.util :only [pp p timeout]]
               [reflex.macros :only [constrain!]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.controls :as controls]))

(constrain!
 (data/load @controls/file-selector
            (partial reset! core/!vcfs)))

;;Request file list from server
(data/available-files (partial reset! core/!available-files))
