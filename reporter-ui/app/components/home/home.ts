import {Component} from 'angular2/core';
import {WrapperCmp} from '../header/header';

@Component({
  selector: 'home',
  templateUrl: './components/home/home.html',
  styleUrls: ['./components/home/home.css'],
  directives: [WrapperCmp]
})
export class HomeCmp {}
