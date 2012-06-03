require 'cucumber/formatter/pretty'

module Qcumberless
  class Formatter < Cucumber::Formatter::Pretty
    def before_step( step )
      @io.printf("Step: ".indent(@scenario_indent + 2))
      @io.printf(step.name)
      @io.printf "\n"
      @io.flush
      super
    end
    def before_step_result(keyword, step_match, multiline_arg, status, exception, source_indent, background)
      if exception
        @io.printf("Step failed: ".indent(@scenario_indent + 2))
        @io.printf(exception)
        @io.printf("\n")
      end
      @io.flush
      super
    end
    def before_outline_table(outline_table)
      @io.printf("Outline table\n".indent(@scenario_indent + 2))
      @io.flush
      super
    end
    def before_table_row(table_row)
      @io.printf("Table row: ".indent(@scenario_indent + 2))
      @io.printf(table_row.name)
      @io.printf "\n"
      @io.flush
      super
    end
  end
end
