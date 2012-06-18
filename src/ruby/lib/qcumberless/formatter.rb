require 'cucumber/formatter/pretty'

module Qcumberless
  class Formatter
    def initialize(step_mother, path_or_io, options)
    end

    def feature_name(keyword, name)
      print("Feature: " + name + "\n")
    end

    def scenario_name(keyword, name, file_colon_line, source_indent)
      print("Scenario: " + name + "\n")
    end

    def before_step(step)
      print("Step: " + step.name + "\n")
    end

    def before_step_result(keyword, step_match, multiline_arg, status, exception, source_indent, background, file_colon_line)
      if exception
        print("Step failed: " + exception + "\n")
      elsif status == :undefined
        print("Step failed: Step undefined\n")
      end
    end

    def before_outline_table(outline_table)
      print("Outline table\n")
      @header_row = true
    end

    def before_table_row(table_row)
      printf("Table row: |")
    end

    def table_cell_value(value, status)
      print(value.to_s + "|")
    end

    def after_table_row(table_row)
      print("\n")
      unless @header_row
        if table_row.exception
          print("Step failed: " + table_row.exception + "\n")
        #elsif table_row.status == :undefined
        #  print("Step failed: Step undefined\n")
        end
      end
      @header_row = false if @header_row
    end
  end
end
