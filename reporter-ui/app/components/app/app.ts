import {Component, ViewEncapsulation} from 'angular2/core';
import {
  RouteConfig,
  ROUTER_DIRECTIVES
} from 'angular2/router';

import {DailyDashboard} from '../dailyDashboard/dailyDashboard';
import {NameList} from '../../services/name_list';
import {Header} from '../header/header';

@Component({
  selector: 'app',
  viewProviders: [NameList],
  templateUrl: './components/app/app.html',
  styleUrls: ['./components/app/app.css'],
  encapsulation: ViewEncapsulation.None,
  directives: [Header, ROUTER_DIRECTIVES]
})
@RouteConfig([
  { path: '/', redirectTo: ['/DailyDashboard'] },
  { path: '/dashboard', component: DailyDashboard, as: 'DailyDashboard' },
])
export class AppCmp {}

