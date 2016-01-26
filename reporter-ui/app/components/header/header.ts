import {Component, ElementRef} from 'angular2/core';
import {CORE_DIRECTIVES} from 'angular2/common';
import {DROPDOWN_DIRECTIVES, ACCORDION_DIRECTIVES} from 'ng2-bootstrap';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
    selector: 'sidebar',
    templateUrl: './components/header/sidebar.html',
    styleUrls: ['./components/header/sidebar.css'],
    directives: [ROUTER_DIRECTIVES]
})
export class Sidebar {
}

@Component({
    selector: 'header',
    templateUrl: './components/header/header.html',
    styleUrls: ['./components/header/header.css'],
    directives: [Sidebar]
})
export class Header {
}

@Component({
    selector: 'header-notification',
    templateUrl: './components/header/header-notification.html',
    directives: [DROPDOWN_DIRECTIVES, ROUTER_DIRECTIVES, CORE_DIRECTIVES],
    viewProviders: [DROPDOWN_DIRECTIVES, ElementRef]
})
export class HeaderNotification {
    toggled(open:boolean):void {
        console.log('Dropdown is now: ', open);
    }
}

