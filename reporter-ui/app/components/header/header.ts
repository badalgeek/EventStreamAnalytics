import {Component, ElementRef} from 'angular2/core';
import {CORE_DIRECTIVES} from 'angular2/common';
import {DROPDOWN_DIRECTIVES, ACCORDION_DIRECTIVES} from 'ng2-bootstrap';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
    selector: 'header',
    templateUrl: './components/header/header.html',
    directives: []
})
export class Header {

}

@Component({
    selector: 'sidebar',
    templateUrl: './components/header/sidebar.html',
    directives: [ROUTER_DIRECTIVES, ACCORDION_DIRECTIVES]
})
export class Sidebar {
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

@Component({
    selector: 'wrapper',
    template: `<div id="wrapper">
      <header></header>
      <div id="page-wrapper" style="min-height: 561px;">
        <ng-content></ng-content>
      </div>
    </div>`,
    directives: [Header, CORE_DIRECTIVES]
})
export class WrapperCmp {
}
