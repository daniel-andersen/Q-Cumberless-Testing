# -*- encoding: utf-8 -*-
$:.push File.expand_path("../lib", __FILE__)
require "qcumberless/version"

Gem::Specification.new do |s|
  s.name        = "qcumberless"
  s.version     = QCumberless::VERSION
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Daniel Andersen"]
  s.email       = ["dani_ande@yahoo.dk"]
  s.homepage    = "https://github.com/black-knight/Q-Cumberless-Testing"
  s.summary     = %q{Cucumber formatter for use with Q-Cumberless Testing}
  s.description = %q{Cucumber formatter for use with Q-Cumberless Testing}

  s.add_dependency "cucumber"

  s.post_install_message = <<-EOS

  *****************************************************************
  * To use the qcumberless formatter, simple add                  *
  *   --format 'QCumberless::Formatter'                           * 
  * to your cucumber.yml, Rakefile, or command line call          *
  *****************************************************************

  EOS

  s.files         = `git ls-files`.split("\n")
  s.test_files    = `git ls-files -- {test,spec,features}/*`.split("\n")
  s.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  s.require_paths = ["lib"]
end
