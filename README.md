## Functions as patterns :rainbow:

Exploring patterns as a means of understanding and documenting functions.

Inspired by the work of Alex McLean ([@yaxu](https://github.com/yaxu)) using visual patterns to explain the live coding language Tidal:
[Tidal Pattern Language for Live Coding of Music](https://www.academia.edu/467099/TIDAL_PATTERN_LANGUAGE_FOR_LIVE_CODING_OF_MUSIC)

### Install:

[![Clojars Project](https://img.shields.io/clojars/v/functions-as-patterns.svg)](https://clojars.org/functions-as-patterns)

### Example:

```clojure
(require 'functions-as-patterns.core :refer :all)

(set-dir! "/tmp/") ;; Where rendered-images get saved

(view
 (partition 2 (partition 3 (hues 10))))
```

__Arg:__

![Argument](https://raw.githubusercontent.com/josephwilk/functions-as-patterns/master/doc/clojure.core%24partition_arg1.png)

__Result:__

![Result](https://raw.githubusercontent.com/josephwilk/functions-as-patterns/master/doc/clojure.core%24partition_post.png)


# License

Copyright © 2017-present Joseph Wilk

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For full license information, see the LICENSE file.
