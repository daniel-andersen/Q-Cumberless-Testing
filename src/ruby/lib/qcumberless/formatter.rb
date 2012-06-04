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
        @io.flush
      elsif status == :undefined
        @io.printf("Step failed: Step undefined\n".indent(@scenario_indent + 2))
        @io.flush
      end
      super
    end

    def before_outline_table(outline_table)
      @io.printf("Outline table\n".indent(@scenario_indent + 2))
      @io.flush
      @header_row = true
      super
    end

    def before_table_row(table_row)
      @io.printf("Table row: ".indent(@scenario_indent + 2))
      @io.printf(table_row.name)
      @io.printf "\n"
      @io.flush
      super
    end

    def after_table_row(table_row)
      unless @header_row
        if table_row.exception
          @io.printf("\nStep failed: ".indent(@scenario_indent + 2))
          @io.printf(table_row.exception)
          @io.printf("\n")
          @io.flush
        elsif table_row.status == :undefined
          @io.printf("\nStep failed: Step undefined\n".indent(@scenario_indent + 2))
          @io.flush
        end
      end
      @header_row = false if @header_row
      super
    end
  end
end
