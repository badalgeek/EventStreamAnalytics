import {Injectable} from 'angular2/core';
import {Http, Headers, Response} from 'angular2/http';
import {Observable} from 'rxjs/Observable' ;
import {TotalCustomer} from './TotalCustomer';
import 'rxjs/add/operator/map';

@Injectable()
export class UserStatisticsService {

    http:Http;
    headers:Headers;

    constructor(http:Http) {
        this.http = http;
        this.headers = new Headers();
        this.headers.set('Content-Type', 'application/json');
    }

    public getCount():Observable<Array<TotalCustomer>> {
        return this.http.get('http://localhost:9000/events/customers').map(
            (response: Response) => response.json());
    }

}
