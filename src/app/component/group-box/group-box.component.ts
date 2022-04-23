import { Component, OnInit, Input} from '@angular/core';

@Component({
  selector: 'group-box',
  templateUrl: './group-box.component.html',
  styleUrls: ['./group-box.component.css']
})
export class GroupBoxComponent implements OnInit {

  @Input() title: String | undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
