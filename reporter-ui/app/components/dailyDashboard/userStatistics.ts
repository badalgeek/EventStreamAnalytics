import {Component} from 'angular2/core';
import {Injectable,Inject} from 'angular2/core';
import {CHART_DIRECTIVES} from 'ng2-charts';
import {CORE_DIRECTIVES, FORM_DIRECTIVES, NgClass} from 'angular2/common';
import {UserStatisticsService} from '../../services/userStatisticsService';
import {TotalCustomer} from '../../services/TotalCustomer';
@Injectable()
@Component({
    selector: 'user-statistics',
    templateUrl: './components/dailyDashboard/userStatistics.html',
    styleUrls: ['./components/dailyDashboard/userStatistics.css'],
    directives: [CHART_DIRECTIVES, NgClass, CORE_DIRECTIVES, FORM_DIRECTIVES]
})
export class UserStatistics {

    // lineChart
    private lineChartData:Array<any> = [
        [65, 59, 80, 81, 56, 55, 40, 65, 59, 80, 81, 56, 55, 40, 65, 59, 80, 81, 56, 55, 40, 65, 59, 80, 81, 56, 55,
            40, 65, 59, 80, 81, 56, 55, 40, 65, 59, 80, 81, 56, 55, 40, 65, 59, 80, 81, 56, 55],
        [48, 48, 60, 61, 46, 45, 32, 48, 48, 60, 61, 46, 45, 32, 48, 48, 60, 61, 46, 45, 32, 48, 48, 60, 61, 46, 45,
            32, 48, 48, 60, 61, 46, 45, 32, 48, 48, 60, 61, 46, 45, 32, 48, 48, 60, 61, 46, 45],
        [10, 6, 15, 12, 6, 5, 4, 10, 6, 15, 12, 6, 5, 4, 10, 6, 15, 12, 6, 5, 4, 10, 6, 15, 12, 6, 5,
            4, 10, 6, 15, 12, 6, 5, 4, 10, 6, 15, 12, 6, 5, 4, 10, 6, 15, 12, 6, 5],
        [5, 5, 5, 8, 4, 5, 4, 5, 5, 5, 8, 4, 5, 4, 5, 5, 5, 8, 4, 5, 4, 5, 5, 5, 8, 4, 5,
            4, 5, 5, 5, 8, 4, 5, 4, 5, 5, 5, 8, 4, 5, 4, 5, 5, 5, 8, 4, 5]
    ];
    private lineChartLabels:Array<any> = [
        '00:00', '00:05', '00:10', '00:15', '00:20', '00:25', '00:30', '00:35', '00:40', '00:45', '00:50', '00:55',
        '01:00', '01:05', '01:10', '01:15', '01:20', '01:25', '01:30', '01:35', '01:40', '01:45', '01:50', '01:55',
        '02:00', '02:05', '01:10', '02:15', '02:20', '02:25', '02:30', '02:35', '02:40', '02:45', '02:50', '02:55',
        '03:00', '03:05', '01:10', '03:15', '03:20', '03:25', '03:30', '03:35', '03:40', '03:45', '03:50', '03:55'
    ];
    private lineChartSeries:Array<any> = ['Total', 'Browser', 'Android', 'IPhone'];
    private lineChartOptions:any = {
        animation: false,
        responsive: true,
        multiTooltipTemplate: '<%if (datasetLabel){%><%=datasetLabel %>: <%}%><%= value %>'
    };
    private lineChartColours:Array<any> = [
        { // green
            fillColor: 'rgba(70,191,189,0.2)',
            strokeColor: 'rgba(70,191,189,1)',
            pointColor: 'rgba(70,191,189,1)',
            pointStrokeColor: '#fff',
            pointHighlightFill: '#fff',
            pointHighlightStroke: 'rgba(70,191,189,0.8)'
        },
        { // blue
            fillColor: 'rgba(151,187,25,0.2)',
            strokeColor: 'rgba(151,187,25,1)',
            pointColor: 'rgba(151,187,25,1)',
            pointStrokeColor: '#fff',
            pointHighlightFill: '#fff',
            pointHighlightStroke: 'rgba(151,187,25,0.8)'
        },
        { // yellow
            fillColor: 'rgba(253,180,92,0.2)',
            strokeColor: 'rgba(253,180,92,1)',
            pointColor: 'rgba(253,180,92,1)',
            pointStrokeColor: '#fff',
            pointHighlightFill: '#fff',
            pointHighlightStroke: 'rgba(253,180,92,0.8)'
        },
        { // red
            fillColor: 'rgba(247,70,74,0.2)',
            strokeColor: 'rgba(247,70,74,1)',
            pointColor: 'rgba(247,70,74,1)',
            pointStrokeColor: '#fff',
            pointHighlightFill: '#fff',
            pointHighlightStroke: 'rgba(247,70,74,0.8)'
        }
    ];
    private lineChartLegend:boolean = true;
    private lineChartType:string = 'Line';

    // events
    chartClicked(e:any) {
        console.log(e);
    }

    chartHovered(e:any) {
        console.log(e);
    }
}
