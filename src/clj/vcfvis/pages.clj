(ns vcfvis.pages
  "Processing and preparation of display and input pages."
  (:use [clojure.java.io])
  (:require [hiccup.core :as hiccup]
            [net.cgrand.enlive-html :as html]
            [ring.middleware.anti-forgery :as anti-forgery]))

(defn- base-page-w-content
  "Update input HTML with specified content at selector"
  [input-html selector new-hiccup-html]
  (-> (html/xml-resource (file input-html))
      (html/transform selector (-> new-hiccup-html
                                   java.io.StringReader.
                                   html/html-resource
                                   html/content))
      html/emit*
      (#(apply str %))))

(defn add-anti-forgery
  "Return main HTML page with submit information."
  [page]
  (base-page-w-content page [:div#anti-forgery]
                       (hiccup/html [:input {:class "hidden"
                                             :name "__anti-forgery-token"
                                             :value anti-forgery/*anti-forgery-token*}])))