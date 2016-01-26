import {Component, ViewEncapsulation} from 'angular2/core';
import {
  RouteConfig,
  ROUTER_DIRECTIVES
} from 'angular2/router';

import {DailyDashboard} from '../dailyDashboard/dailyDashboard';
import {BrowserDashboard} from '../browserDashboard/browserDashboard';
import {AndroidDashboard} from '../androidDashboard/androidDashboard';
import {IOSDashboard} from '../iOSDashboard/iOSDashboard';
import {WindowsPhoneDashboard} from '../windowsPhoneDashboard/windowsPhoneDashboard';
import {Header} from '../header/header';
import {EventComparison} from '../eventComparison/eventComparison';

@Component({
  selector: 'app',
  viewProviders: [],
  templateUrl: './components/app/app.html',
  styleUrls: ['./components/app/app.css'],
  encapsulation: ViewEncapsulation.None,
  directives: [Header, ROUTER_DIRECTIVES]
})
@RouteConfig([
  { path: '/', redirectTo: ['/DailyDashboard'] },
  { path: '/dashboard', component: DailyDashboard, as: 'DailyDashboard' },
  { path: '/browserDashboard', component: BrowserDashboard, as: 'BrowserDashboard' },
  { path: '/androidDashboard', component: AndroidDashboard, as: 'AndroidDashboard' },
  { path: '/iOSDashboard', component: IOSDashboard, as: 'IOSDashboard' },
  { path: '/windowsPhoneDashboard', component: WindowsPhoneDashboard, as: 'WindowsPhoneDashboard' },
  { path: '/eventComparison', component: EventComparison, as: 'EventComparison' }
])
export class AppCmp {}

