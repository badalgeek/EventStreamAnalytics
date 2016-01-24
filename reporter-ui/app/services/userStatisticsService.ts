import {Request, Response, Http} from 'angular2/http';
import {Injectable} from 'angular2/core';
import {RESTClient, GET, PUT, POST, DELETE, BaseUrl, Headers, DefaultHeaders, Path, Body, Query} from 'angular2-rest';
import {Constants} from '../StageSettings/Constants';
import { Observable} from 'rxjs/Observable' ;
import {TotalCustomer} from './TotalCustomer';
@Injectable()
@BaseUrl ( 'http://http://localhost:9000/')
@DefaultHeaders({
    'Accept': 'application/json',
    'Content-Type': 'application/json'
})
export class UserStatisticsService extends RESTClient {

  @GET('events/customers')
   public getCount(): Observable<Array<TotalCustomer>> { return null; };

}
