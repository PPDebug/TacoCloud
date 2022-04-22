import { TestBed } from '@angular/core/testing';

import { TacoService } from './taco.service';

describe('TacoService', () => {
  let service: TacoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TacoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
