(ns vcfvis.ui
  (:use-macros [c2.util :only [pp p bind!]]
               [reflex.macros :only [computed-observable constrain!]])
  (:require [vcfvis.core :as core]
            [c2.dom :as dom]))

(let [$dd (dom/select "#user-dropdown")]
  (constrain!
   (dom/text $dd (pp (get @core/!context :username "USER")))))
