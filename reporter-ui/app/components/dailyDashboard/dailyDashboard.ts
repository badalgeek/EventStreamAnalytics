import {Component} from 'angular2/core';
import {UserStatistics} from './userStatistics';

@Component({
    selector: 'home',
    templateUrl: './components/dailyDashboard/dailyDashboard.html',
    styleUrls: ['./components/dailyDashboard/dailyDashboard.css'],
    directives: [UserStatistics]
})
export class DailyDashboard {
}

