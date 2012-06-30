(ns vcfvis.main
  (:use-macros [c2.util :only [pp p]]
               [reflex.macros :only [constrain!]])
  (:require [vcfvis.core :as core]
            [vcfvis.data :as data]
            [vcfvis.controls :as controls]))


(constrain!
 (reset! core/!vcfs
         (doall (map data/load
                     (remove nil? (distinct (map deref controls/selectors)))))))

(reset! core/!available-filenames (data/available-filenames))


(constrain!
 (pp @core/!vcfs))

(constrain!
 (pp @core/!shared-metrics))
