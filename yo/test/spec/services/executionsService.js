'use strict';

describe('Service: Executionsservice', function () {

  // load the service's module
  beforeEach(module('yoApp'));

  // instantiate service
  var Executionsservice;
  beforeEach(inject(function (_Executionsservice_) {
    Executionsservice = _Executionsservice_;
  }));

  it('should do something', function () {
    expect(!!Executionsservice).toBe(true);
  });

});
