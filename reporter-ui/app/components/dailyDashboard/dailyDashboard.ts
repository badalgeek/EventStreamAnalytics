import {Component} from 'angular2/core';
import {NgFor} from 'angular2/common';
import {UserStatistics} from './userStatistics';
import {UserStatisticsService} from '../../services/userStatisticsService';
import {TotalCustomer} from '../../services/TotalCustomer';

@Component({
    selector: 'home',
    templateUrl: './components/dailyDashboard/dailyDashboard.html',
    styleUrls: ['./components/dailyDashboard/dailyDashboard.css'],
    directives: [UserStatistics, NgFor],
    providers: [UserStatisticsService]
})
export class DailyDashboard {

    userStatisticsService:UserStatisticsService;
    totalCustomerList:Array<TotalCustomer>;

    constructor(userStatisticsService:UserStatisticsService) {
        this.userStatisticsService = userStatisticsService;
        this.getCount();
    }

    private getCount() {
        return this.userStatisticsService.getCount().subscribe(
            val => {
                this.totalCustomerList = val;
            }, ex => {
                console.error('Error in service call' + ex);
            }
        );
    }
}

