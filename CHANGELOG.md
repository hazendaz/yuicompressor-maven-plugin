2.0.0-M1
--------

* Rebranded repository under 'hazendaz' to allow it to work properly with maven 3.9 and 4.0 without issuing warnings or potentially breaking with 4.0.
* Completely overhauled the project including running many code cleanups up it to use more modern Java
* Confirmed working for java 11, 17, 20, and 21.
* Maven expectation is for 3.9 and above but should work on older versions but not confirmed.
* Yui compressor included is at 2.4.7 due to an edge case bug, override it if you don't have that condition, see issues.


1.3.1
--------------------

+ Upgraded YUI Compressor to version 2.4.7 with following changes:
    + Handle data urls without blowing up Java memory (regex)
    + Updated docs to reflect Java >= 1.5 required for CssCompressor
    + Fixed issue where we were breaking #AABBCC id selectors, with the #AABBCC -> #ABC color compression.

+ Changed default value for linebreakpos to -1, as currently default 0 have a special meaning (Fixed #31)

        YUI Compressor README:
        Some source control tools don't like files containing lines longer than,
        say 8000 characters. The linebreak option is used in that case to split
        long lines after a specific column. It can also be used to make the code
        more readable, easier to debug (especially with the MS Script Debugger)
        Specify 0 to get a line break after each semi-colon in JavaScript, and
        after each rule in CSS.

+ Added detailed CHANGELOG
