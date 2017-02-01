(ns functions-as-patterns.color
  (require [mikera.image.core :refer :all]
           [mikera.image.colours :refer :all]
           [com.evocomputing.colors :as colors]))

(def blank-color     (colors/create-color "#663477"))
(def highlight-color (colors/create-color "#EE5C96"))
(def stroke-color    (colors/create-color "#111111"))
(def text-color (colors/create-color "#ffffff"))

(def darker-highlight-color (colors/adjust-hue (colors/create-color "#3A396C") -35))

(def rgb-blank-color     (colors/rgba-int blank-color))
(def rgb-highlight-color (colors/rgba-int highlight-color))
(def rgb-darker-highlight-color (colors/rgba-int darker-highlight-color))
(def rgb-stroke-color    (colors/rgba-int stroke-color))
(def rgb-text-color (colors/rgba-int text-color))

(def rect-start 70)
(def stroke-size 2)

(defn int->color [i]
  (-> highlight-color
      (colors/adjust-hue (* i -50))
      colors/rgba-int))

(defn hues
  ([steps] (hues 25 steps highlight-color))
  ([steps factor] (hues factor steps highlight-color))
  ([steps factor base]
   (-> (map
        (fn [hue-adjust] (colors/rgba-int
                         (colors/adjust-hue base hue-adjust)))
        (range 0 (* steps factor) steps))
       vec)))

(defn color-seq
  ([n] (color-seq n rgb-blank-color))
  ([n color] (take n (cycle [color]))))

(defn- container-color [depth]
  (-> blank-color
      (colors/adjust-hue (* depth -40))
      colors/rgba-int))

(defn- flatten-all [coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (if (coll? (first s))
       (concat (flatten (first s)) (flatten-all (rest s)))
       (cons (first s) (flatten-all (rest s)))))))

(defn- no-of-leaf-nodes [col]
  (if (sequential? col)
    (count (flatten-all col))
    0))

(defn- fill-round-rect!
  ([image x y w h colour]
   (let [g (graphics image)
         ^Color colour (to-java-color colour)]
     (.setColor g colour)
     (.fillRect g (int x) (int y) (int w) (int h))
     image)))

(defn- draw-chars! [image text x y w h colour]
  (let [g (graphics image)
        ^Color colour (to-java-color colour)]
    (.setColor g colour)
    (.drawChars g (char-array text) 0 (count (char-array text)) (int (+ (/ (- w (* 5 (count (char-array text))) ) 2) x)) (int (+ (/ h 2) y)))
    image))

(defn- find-color [color-lookup v] (get color-lookup v v))

(defn- paint-stroked-rectangle! [img color-lookup seq-value posx posy rect-w rect-h]
  (let [x (+ posx stroke-size)
        y (+ posy stroke-size)
        w (- rect-w stroke-size)
        h (- rect-h (* 2 stroke-size))
        color (find-color color-lookup seq-value)]

    (fill-round-rect! img
                posx posy
                (+ w (* 2 stroke-size)) (+ (* 2 stroke-size) h)
                rgb-stroke-color)
    (fill-round-rect! img
                x y
                w h
                color)
    (comment
      ;; debug
      (draw-chars! img (str posx) x y w h rgb-text-color))))

(defn- leaf?     [node] (not (sequential? node)))
(defn- children? [node] (sequential? node))

(defn- paint-rectangle! [img color-lookup color rect-size depth x-offset]
  (let [y-offset (/ (- rect-start rect-size) 2)]
    (if (leaf? color)
      (paint-stroked-rectangle! img color-lookup color x-offset y-offset rect-size rect-size)
      (let [width (* (no-of-leaf-nodes color) rect-size)]
        (paint-stroked-rectangle! img color-lookup (container-color depth) x-offset y-offset width rect-size)))))

(defn- paint-all! [img color-lookup rect-size x-offset depth]
  (fn [parent-indent [idx color]]
    (paint-rectangle! img color-lookup color rect-size depth parent-indent)

    (if (children? color)
      (let [new-rect-size (/ rect-size 2)
            indent (/ (* rect-size (no-of-leaf-nodes color))
                      2 2)]
        (+
         indent
         (reduce
          (paint-all! img color-lookup new-rect-size parent-indent (inc depth))
          (+ indent parent-indent)
          (map vector (range) color))))
      (+ parent-indent rect-size)
      )))

(defn- render [data color-lookup dir title]
  (let [rect-size rect-start
        total-cells  (no-of-leaf-nodes data)
        bi (new-image (+ (* total-cells rect-size) stroke-size) rect-size)]

    (fill! bi (colors/rgba-int stroke-color))
    (reduce (paint-all! bi color-lookup rect-size 0 0)
            0
            (map vector (range) data))
    (if (clojure.string/blank? dir)
      (show bi :zoom 1.0 :title title)
      (save bi (str dir "/" title ".png")))))

(defn- fn->str [fn-to-convert]
  (-> (str fn-to-convert)
      (clojure.string/split #"@")
      first
      (clojure.string/replace #"__4385" "") ;;Some odd clojure fn noise?
      (clojure.string/replace #"__4331" "")
      (clojure.string/replace #"__4345" "")
      ))

(defn- color->rgba [c]
  (if (= (type c)
         :com.evocomputing.colors/color)
    (colors/rgba-int c)
    c))

(defn- render-fn
  ([fn-to-doc color-map out dir & args]
   (let [name (fn->str fn-to-doc)
         args (->>
               args
               (map (fn [a] (if (and (sequential? a) (= 1 (no-of-leaf-nodes a))) [a]  a )))
               (map (fn [a] (if (sequential? a) a [a])))
               (map (fn [args] (map color->rgba args))))
         out (map color->rgba out)]
     (dotimes [i (count args)]
       (try
         (render (nth args i)  color-map dir (str name "_arg" i))
         (catch Exception e (println "Unable to render:" (nth args i)))))
     (render out color-map dir (str name "_post")))))

(defn example->color
  "Assumes arguments are colors"
  [{fn-to-doc :fn args :args dir :dir color-map :colors}]
  (let [args (vec args)
        out (apply fn-to-doc args)
       color-map (or color-map {})]
    (apply render-fn fn-to-doc color-map out dir args)))

(defn example->forced-color
  "Forces any sequences into color values."
  [{fn-to-doc :fn args :args dir :dir}]
  (let [args (vec args)
        all-args (flatten (remove (fn [a] (not (sequential? a))) args))
        all-hues (hues (count all-args))
        colors (reduce (fn [acc [idx v]]
                         (assoc acc v (get acc v (nth all-hues idx))))
                       {}
                       (map vector (range) all-args))]
    (example->color {:fn fn-to-doc :args args :dir dir :colors colors})))
