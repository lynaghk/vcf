(ns vcfvis.controls
  (:use-macros [c2.util :only [pp p]])
  (:use [chosen.core :only [ichooseu! options selected]])
  (:require [vcfvis.data :as data]
            [c2.dom :as dom]))

(let [$selectors (dom/select ".file-selectors")]
  (dotimes [i 2]
    (let [$sel (dom/append! $selectors [:select])
          !c (ichooseu! $sel)]
      (options !c ["File 1" "File 2"]))))
