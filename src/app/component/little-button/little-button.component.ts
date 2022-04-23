import { Component, OnInit, Input,Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'little-button',
  templateUrl: './little-button.component.html',
  styleUrls: ['./little-button.component.css']
})
export class LittleButtonComponent implements OnInit {

  @Input() label: String | undefined;

  @Output()
  public readonly buttonEmit: EventEmitter<String> = new EventEmitter<String>();

  constructor() { 
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.buttonEmit.emit(this.label);
  }
  

}
