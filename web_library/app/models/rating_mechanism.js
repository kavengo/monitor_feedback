var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
define(["require", "exports", './mechanism'], function (require, exports, mechanism_1) {
    "use strict";
    var RatingMechanism = (function (_super) {
        __extends(RatingMechanism, _super);
        function RatingMechanism(type, active, order, canBeActivated, parameters) {
            _super.call(this, type, active, order, canBeActivated, parameters);
            this.currentRatingValue = this.getParameterValue('defaultRating');
        }
        RatingMechanism.prototype.getRatingElementOptions = function () {
            var ratingMechanismObject = this;
            return {
                starSize: 25,
                totalStars: this.getParameterValue('maxRating'),
                initialRating: ratingMechanismObject.currentRatingValue,
                useFullStars: true,
                disableAfterRate: false,
                callback: function (currentRating, $el) {
                    ratingMechanismObject.currentRatingValue = currentRating;
                }
            };
        };
        return RatingMechanism;
    }(mechanism_1.Mechanism));
    exports.RatingMechanism = RatingMechanism;
});
//# sourceMappingURL=rating_mechanism.js.map