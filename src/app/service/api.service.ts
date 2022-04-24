import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  rootUrl = "http://localhost:8080/rest/";

  private headers = new HttpHeaders()
  // content-type
  .append("content-type", "application/json")
  // COSR
  .append("Access-Control-Allow-Origin","*")
  .append("Access-Control-Allow-Methods","GET, POST, PUT, OPTIONS, PATCH, DELETE")
  .append("Access-Control-Allow-Headers","X-Requested-With, content-type");

  constructor(
    private httpClient: HttpClient
    ) { }

  public getCommon(subUrl: String){
    return this.httpClient.get(this.rootUrl + subUrl, 
      {
      headers: this.headers,
    });
    // for test:
    // return this.httpClient.get("/assets/mocks/" + subUrl + ".json");
  }

  public postCommon(subUrl:String, data: any) {
    console.log("api post: ", data);
    return this.httpClient.post(
      this.rootUrl + subUrl,
      data,
      {
        headers: this.headers
      }
    );
  }

}
