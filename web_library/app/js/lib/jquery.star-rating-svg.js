!function (a) {
    "use strict";
    var b = "starRating", c = function () {
    }, d = {
        totalStars: 5,
        useFullStars: !1,
        emptyColor: "lightgray",
        hoverColor: "orange",
        activeColor: "gold",
        useGradient: !0,
        readonly: !1,
        disableAfterRate: !0,
        starGradient: {start: "#FEF7CD", end: "#FF9511"},
        strokeWidth: 0,
        strokeColor: "black",
        initialRating: 0,
        starSize: 40,
        callback: c,
        onHover: c,
        onLeave: c
    }, e = function (c, e) {
        var f;
        this.element = c, this.$el = a(c), this.settings = a.extend({}, d, e), f = this.$el.data("rating") || this.settings.initialRating, this._state = {rating: (Math.round(2 * f) / 2).toFixed(1)}, this._uid = Math.floor(999 * Math.random()), e.starGradient || this.settings.useGradient || (this.settings.starGradient.start = this.settings.starGradient.end = this.settings.activeColor), this._defaults = d, this._name = b, this.init()
    }, f = {
        init: function () {
            this.renderMarkup(), this.addListeners(), this.initRating()
        }, addListeners: function () {
            this.settings.readOnly || (this.$stars.on("mouseover", this.hoverRating.bind(this)), this.$stars.on("mouseout", this.restoreState.bind(this)), this.$stars.on("click", this.applyRating.bind(this)))
        }, hoverRating: function (a) {
            var b = this.getIndex(a);
            this.paintStars(b, "hovered"), this.settings.onHover(b + 1, this._state.rating, this.$el)
        }, applyRating: function (a) {
            var b = this.getIndex(a), c = b + 1;
            this.paintStars(b, "active"), this.executeCallback(c, this.$el), this._state.rating = c, this.settings.disableAfterRate && this.$stars.off()
        }, restoreState: function (a) {
            var b = this.getIndex(a), c = this._state.rating || -1;
            this.paintStars(c - 1, "active"), this.settings.onLeave(b + 1, this._state.rating, this.$el)
        }, getIndex: function (b) {
            var c = a(b.currentTarget), d = c.width(), e = b.offsetX < d / 2 && !this.settings.useFullStars ? "left" : "right", f = c.index() - ("left" === e ? .5 : 0);
            return f = 0 > f && b.offsetX < d / 5 ? -1 : f
        }, initRating: function () {
            this.paintStars(this._state.rating - 1, "active")
        }, paintStars: function (b, c) {
            var d, e, f, g;
            a.each(this.$stars, function (h, i) {
                d = a(i).find('polygon[data-side="left"]'), e = a(i).find('polygon[data-side="right"]'), f = g = b >= h ? c : "empty", f = h - b === .5 ? c : f, d.attr("class", "svg-" + f + "-" + this._uid), e.attr("class", "svg-" + g + "-" + this._uid)
            }.bind(this))
        }, renderMarkup: function () {
            for (var a = '<div class="jq-star" style="width:' + this.settings.starSize + "px;  height:" + this.settings.starSize + 'px;"><svg version="1.0" class="jq-star-svg" shape-rendering="geometricPrecision" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="305px" height="305px" viewBox="60 -62 309 309" style="enable-background:new 64 -59 305 305; stroke-width:' + this.settings.strokeWidth + 'px;" xml:space="preserve"><style type="text/css">.svg-empty-' + this._uid + "{fill:url(#" + this._uid + "_SVGID_1_);}.svg-hovered-" + this._uid + "{fill:url(#" + this._uid + "_SVGID_2_);}.svg-active-" + this._uid + "{fill:url(#" + this._uid + "_SVGID_3_);}</style>" + this.getLinearGradient(this._uid + "_SVGID_1_", this.settings.emptyColor, this.settings.emptyColor) + this.getLinearGradient(this._uid + "_SVGID_2_", this.settings.hoverColor, this.settings.hoverColor) + this.getLinearGradient(this._uid + "_SVGID_3_", this.settings.starGradient.start, this.settings.starGradient.end) + '<polygon data-side="left" class="svg-empty-' + this._uid + '" points="281.1,129.8 364,55.7 255.5,46.8 214,-59 172.5,46.8 64,55.4 146.8,129.7 121.1,241 213.9,181.1 213.9,181 306.5,241 " style="stroke: ' + this.settings.strokeColor + '"/><polygon data-side="right" class="svg-empty-' + this._uid + '" points="364,55.7 255.5,46.8 214,-59 213.9,181 306.5,241 281.1,129.8 " style="stroke-dasharray: 230 232 210 0; stroke: ' + this.settings.strokeColor + '"/></svg></div>', b = "", c = 0; c < this.settings.totalStars; c++)b += a;
            this.$el.append(b), this.$stars = this.$el.find(".jq-star")
        }, getLinearGradient: function (a, b, c) {
            return '<linearGradient id="' + a + '" gradientUnits="userSpaceOnUse" x1="121.1501" y1="-70.35" x2="121.15" y2="125.0045"><stop  offset="0" style="stop-color:' + b + '"/><stop  offset="1" style="stop-color:' + c + '"/> </linearGradient>'
        }, executeCallback: function (a, b) {
            var c = this.settings.callback;
            c(a, b)
        }
    }, g = {
        unload: function () {
            var c = "plugin_" + b, d = a(this), e = d.data(c).$star;
            d.removeData(c), e.off()
        }
    };
    a.extend(e.prototype, f), a.fn[b] = function (c) {
        return !a.isPlainObject(c) && g.hasOwnProperty(c) ? void g[c].apply(this) : this.each(function () {
            a.data(this, "plugin_" + b) || a.data(this, "plugin_" + b, new e(this, c))
        })
    }
}(jQuery, window, document);