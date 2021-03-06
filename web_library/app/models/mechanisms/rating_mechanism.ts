import {Mechanism} from './mechanism';
import {Parameter} from '../parameters/parameter';


export class RatingMechanism extends Mechanism {
    currentRatingValue:number;
    initialRating:number;

    constructor(id: number, type:string, active:boolean, order?:number, canBeActivated?:boolean, parameters?:Parameter[]) {
        super(id, type, active, order, canBeActivated, parameters);
        this.initialRating = this.getParameterValue('defaultRating');
        this.currentRatingValue = this.getParameterValue('defaultRating');
    }

    getRatingElementOptions(): {} {
        var ratingMechanismObject = this;

        return {
            starSize: 30,
            baseUrl: true,
            totalStars: this.getParameterValue('maxRating'),
            initialRating: this.initialRating,
            useFullStars: true,
            disableAfterRate: false,
            callback: function (currentRating, $el) {
                var oldRatingValue = ratingMechanismObject.currentRatingValue;
                ratingMechanismObject.currentRatingValue = currentRating;
                if(oldRatingValue === currentRating) {
                    $el.starRating('setRating', 0);
                    ratingMechanismObject.currentRatingValue = 0;
                }
            }
        };
    }

    getContext(): any {
        return {
            title: this.getParameterValue('title'),
            boxWidth: this.getParameterValue('boxWidth') || '100%',
            mandatory: this.getParameterValue('mandatory'),
            mandatoryReminder: this.getParameterValue('mandatoryReminder'),
            boxPaddingLeft: this.getParameterValue('boxPaddingLeft') || '0',
            boxPaddingRight: this.getParameterValue('boxPaddingRight') || '20px'
        }
    }
}