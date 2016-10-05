import {Component, OnInit} from '@angular/core';
import {Feedback} from '../shared/models/feedbacks/feedback';
import {Application} from '../shared/models/applications/application';
import {FeedbackListService} from '../shared/services/feedback-list.service';
import {ApplicationService} from '../shared/services/application.service';
import {TextMechanism} from '../shared/models/mechanisms/text_mechanism';
import {RatingMechanism} from '../shared/models/mechanisms/rating_mechanism';


/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'sd-feedback-list',
  templateUrl: 'feedback-list.component.html',
  styleUrls: ['feedback-list.component.css'],
})

export class FeedbackListComponent implements OnInit {
  errorMessage:string;
  feedbacks:Feedback[] = [];
  filteredFeedbacks:Feedback[] = [];
  applications:Application[] = [];
  sortOrder:{} = {'id': '', 'title': '', 'date': ''};

  constructor(public feedbackListService:FeedbackListService, private applicationService:ApplicationService) {
  }

  ngOnInit() {
    this.getFeedbacks();
    this.getApplications();
  }

  getFeedbacks() {
    this.feedbackListService.get()
      .subscribe(
        feedbacks => {
          this.feedbacks = feedbacks;
          this.filteredFeedbacks = feedbacks;
          this.sortFeedbacks('id', false);
        },
        error => this.errorMessage = <any>error
      );
  }

  getApplications() {
    this.applicationService.all().subscribe(
      applications => {
        this.applications = applications;
        for (var application of this.applications) {
          application.filterActive = false;
        }
        this.populateConfigurationData();
      },
      error => this.errorMessage = <any>error
    );
  }

  resetSortOrder():void {
    this.sortOrder = {'id': '', 'title': '', 'date': ''};
  }

  sortClicked(field:string) {
    if (this.sortOrder[field] === '') {
      this.resetSortOrder();
      this.sortFeedbacks(field, true);
      this.sortOrder[field] = 'asc';
    } else if (this.sortOrder[field] === 'asc') {
      this.resetSortOrder();
      this.sortFeedbacks(field, false);
      this.sortOrder[field] = 'desc';
    } else if (this.sortOrder[field] === 'desc') {
      this.resetSortOrder();
      this.sortOrder[field] = 'asc';
      this.sortFeedbacks(field, true);
    }
  }

  sortFeedbacks(field:string, ascending:boolean = true) {
    var feedbacks = this.feedbacks.sort(function (feedbackA, feedbackB) {
      if (field === 'date') {
        var dateA = new Date(feedbackA[field]);
        var dateB = new Date(feedbackB[field]);
        return dateA - dateB;
      } else {
        return feedbackA[field] - feedbackB[field];
      }
    });

    if (!ascending) {
      this.filteredFeedbacks = feedbacks.reverse();
    } else {
      this.filteredFeedbacks = feedbacks;
    }
  }

  search(filterString:string) {
    this.filteredFeedbacks = this.feedbacks.filter(item => item.title.toLowerCase().indexOf(filterString.toLowerCase()) !== -1);
  }

  clickedApplicationFilter(application) {
    var wasActive = application.filterActive;

    for (let application of this.applications) {
      application.filterActive = false;
    }
    application.filterActive = !wasActive;
    if (application.filterActive) {
      this.filteredFeedbacks = this.feedbacks.filter(feedback => feedback.applicationId === application.id);
    } else {
      this.filteredFeedbacks = this.feedbacks;
    }
  }

  /**
   * combines repository with the orchestrator data
   */
  populateConfigurationData() {
    for(var feedback of this.feedbacks) {
      var application = this.applications.filter(application => application.id === feedback.applicationId)[0];
      if(application) {
        var configuration = application.configurations.filter(configuration => configuration.id === feedback.configurationId)[0];

        for(var textFeedback of feedback.textFeedbacks) {
          let textMechanism:TextMechanism = <TextMechanism>configuration.mechanisms.filter(mechanism => mechanism.id === textFeedback.mechanismId)[0];
          textFeedback.mechanism = new TextMechanism(textMechanism.id, textMechanism.type, textMechanism.active, textMechanism.order, textMechanism.canBeActivated, textMechanism.parameters);
        }
        for(var ratingFeedback of feedback.ratingFeedbacks) {
          let ratingMechanism:RatingMechanism = <RatingMechanism>configuration.mechanisms.filter(mechanism => mechanism.id === ratingFeedback.mechanismId)[0];
          ratingFeedback.mechanism = new RatingMechanism(ratingMechanism.id, ratingMechanism.type, ratingMechanism.active, ratingMechanism.order, ratingMechanism.canBeActivated, ratingMechanism.parameters);
        }
      }
    }
  }

  exportAsCSV():void {
    var csvContent = "";
    csvContent += "ID, Title, Date, Application, Feedbacks \n";

    for (let feedback of this.filteredFeedbacks) {
      csvContent += feedback.id + "," + feedback.title + "," + feedback.createdAt + "," + feedback.applicationId + ",";

      for(let textFeedback of feedback.textFeedbacks) {
        if(textFeedback.mechanism) {
          csvContent += textFeedback.mechanism.getParameterValue('title') + ": " + textFeedback.text + ",";
        } else {
          csvContent += textFeedback.text + ",";
        }
      }

      for(let ratingFeedback of feedback.ratingFeedbacks) {
        if(ratingFeedback.mechanism) {
          csvContent += ratingFeedback.mechanism.getParameterValue('title') + ": " + ratingFeedback.rating + "/" + ratingFeedback.mechanism.getParameterValue('maxRating') + ",";
        } else {
          csvContent += ratingFeedback.rating + ",";
        }
      }

      // remove last comma
      csvContent = csvContent.slice(0, -1);
      csvContent += "\n";
    }

    var title = 'feedbacks';
    var filename = title.replace(/ /g, '') + '.csv';
    var blob = new Blob([csvContent], {"type": 'text/csv;charset=utf-8;'});
    if (navigator.msSaveBlob) { // IE 10+
      navigator.msSaveBlob(blob, filename);
    } else {
      var link = document.createElement("a");
      if (link.download !== undefined)
      {
        var url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  }
}

















