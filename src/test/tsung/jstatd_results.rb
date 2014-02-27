#!/usr/bin/env ruby

require 'optparse'
require 'ostruct'
require 'csv'
require 'pp'

class JStatdRewriter
  def initialize(parser, options)
    @minmax_enabled = options[:minmax]
    @perm_gen       = options[:perm_gen]
    @old_gen        = options[:old_gen]
    @fgct           = options[:fgct]
    @gct            = options[:gct]

    prefix = parser.get_file
    if parser.get_file[-4..-1] == '.csv'
      prefix = parser.get_file[0..-4]
    end
    
    if options[:perm_gen]
      puts "Writing to #{prefix}_minmax_permgen.csv"
      
      # Do the processing for the 
      CSV.open("#{prefix}_minmax_permgen.csv", "wb") do |csv|
        minarr = parser.get_column_sample_min(:perm_gen)
        maxarr = parser.get_column_sample_max(:perm_gen)
        
        if minarr.length > maxarr.length
          outer = minarr
          inner = maxarr
          inner_prefix = "Max"
          outer_prefix = "Min"
        else
          outer = maxarr
          inner = minarr
          inner_prefix = "Min"
          outer_prefix = "Max"
        end
        
        # Handle headers
        header    = outer.shift
        header[1] = "#{outer_prefix} #{header[1]}"
        inner_row = inner.shift
        header.insert 2, "#{inner_prefix} #{inner_row[1]}"
        
        csv << header
        
        inner_row  = inner.shift
        prev_inner = nil
        prev_outer = nil
        outer.each do |outer_row|
          outer_row.insert 2, prev_inner
          while !inner_row.nil? && outer_row[0] > inner_row[0]
            prev_inner = inner_row[1]
            inner_row.insert 1, prev_outer
            csv << inner_row
            inner_row = inner.shift
          end
          csv << outer_row
          prev_outer = outer_row[1]
        end      
      end
    end

    if options[:old_gen]
      puts "Writing to #{prefix}_minmax_oldgen.csv"
      CSV.open("#{prefix}_minmax_oldgen.csv", "wb") do |csv|
        minarr = parser.get_column_sample_min(:old_gen)
        maxarr = parser.get_column_sample_max(:old_gen)
        minarr.each do |row|
          p row
        end
        
        if minarr.length > maxarr.length
          outer = minarr
          inner = maxarr
          inner_prefix = "Max"
          outer_prefix = "Min"
        else
          outer = maxarr
          inner = minarr
          inner_prefix = "Min"
          outer_prefix = "Max"
        end
        
        # Handle headers
        header    = outer.shift
        header[1] = "#{outer_prefix} #{header[1]}"
        inner_row = inner.shift
        header.insert 2, "#{inner_prefix} #{inner_row[1]}"
        
        csv << header
        
        inner_row  = inner.shift
        prev_inner = nil
        prev_outer = nil
        outer.each do |outer_row|
          outer_row.insert 2, prev_inner
          while !inner_row.nil? && outer_row[0] > inner_row[0]
            prev_inner = inner_row[1]
            inner_row.insert 1, prev_outer
            csv << inner_row
            inner_row = inner.shift
          end
          csv << outer_row
          prev_outer = outer_row[1]
        end      
        
      end      
    end
    
    if options[:fgct]
      puts "Writing to #{prefix}_minmax_fgct.csv"
      CSV.open("#{prefix}_minmax_fgct.csv", "wb") do |csv|
        minarr = parser.get_column_sample_min(:fgct)
        maxarr = parser.get_column_sample_max(:fgct)
        
        if minarr.length > maxarr.length
          outer = minarr
          inner = maxarr
          inner_prefix = "Max"
          outer_prefix = "Min"
        else
          outer = maxarr
          inner = minarr
          inner_prefix = "Min"
          outer_prefix = "Max"
        end
        
        # Handle headers
        header    = outer.shift
        header[1] = "#{outer_prefix} #{header[1]}"
        inner_row = inner.shift
        header.insert 2, "#{inner_prefix} #{inner_row[1]}"
        
        csv << header
        
        inner_row  = inner.shift
        prev_inner = nil
        prev_outer = nil
        outer.each do |outer_row|
          outer_row.insert 2, prev_inner
          while !inner_row.nil? && outer_row[0] > inner_row[0]
            prev_inner = inner_row[1]
            inner_row.insert 1, prev_outer
            csv << inner_row
            inner_row = inner.shift
          end
          csv << outer_row
          prev_outer = outer_row[1]
        end      
        
      end 
    end     
  
    if options[:gct]
      puts "Writing to #{prefix}_minmax_gct.csv"
      CSV.open("#{prefix}_minmax_gct.csv", "wb") do |csv|
        minarr = parser.get_column_sample_min(:gct)
        maxarr = parser.get_column_sample_max(:gct)
        
        if minarr.length > maxarr.length
          outer = minarr
          inner = maxarr
          inner_prefix = "Max"
          outer_prefix = "Min"
        else
          outer = maxarr
          inner = minarr
          inner_prefix = "Min"
          outer_prefix = "Max"
        end
        
        # Handle headers
        header    = outer.shift
        header[1] = "#{outer_prefix} #{header[1]}"
        inner_row = inner.shift
        header.insert 2, "#{inner_prefix} #{inner_row[1]}"
        
        csv << header
        
        inner_row  = inner.shift
        prev_inner = nil
        prev_outer = nil
        outer.each do |outer_row|
          outer_row.insert 2, prev_inner
          while !inner_row.nil? && outer_row[0] > inner_row[0]
            prev_inner = inner_row[1]
            inner_row.insert 1, prev_outer
            csv << inner_row
            inner_row = inner.shift
          end
          csv << outer_row
          prev_outer = outer_row[1]
        end      
        
      end      
    end
  end
end


class JStatdColumnMetadata
  
  def initialize(interval)
    @interval = interval
    @total = 0
    @count = 0
    @max   = 0
    @min   = 0
    @variance = 0
  end
  
  def add_row(data)
    if !data.is_a? Float
      @header = data
      return data
    end

    @total += data
    @count += 1
    if (data > @max) 
      @max = data
    end
    
    if (data < @min)
      @min = data
    end
  end

  def mean
    return @total / @count if @count > 0
    return 0
  end

  def stddev(values)
    return Math.sqrt(self.variance(values))
  end
  
  def total
    return @total
  end
  
  def max
    return @max
  end

  def min
    return @min
  end

  def variance(values)
    if !values.nil?
      @variance = 0

      values.each do |val|
        diff = mean - val
        @variance += (diff * diff)
      end
    end

    return @variance / @count
  end

  def duration
    return (@count * @interval)
  end

  def reinterval
    return (@count * @interval) / 20
  end

end

class JStatdParser 
  
  public
  def initialize(csv,interval)
    @csv = csv
    @interval = interval
    @columns = []
    self.parse()
  end

  def get_file
    @csv
  end

  def update_column_metadata(index, column)
    metadata = @columns[index]
    if (metadata.nil?) 
      metadata = JStatdColumnMetadata.new(@interval)
      @columns[index] = metadata
    end    
    
    metadata.add_row(column) if !column.nil?
  end

  def header_from(name)
    return { :perm_gen => [4, 'Perm Gen'], 
             :old_gen  => [3, 'Old Gen' ],
             :fgct     => [8, 'Full Collection Time'],
             :gct      => [9, 'Garbage Collection Time']}[name]

  end
  
  def is_peak(tuple)
    return is_peak2(tuple, 1)
  end

  def is_peak2(tuple, direction)
    if (tuple[1] * direction) > (tuple[0] * direction) && (tuple[1] * direction) >= (tuple[2] * direction)
      return true
    end
    return false
  end

  def get_column_sample(name)
    header = header_from(name)
    index  = header[0]
    retval = []
    prev = [ 0, 0, 0 ]

    retval.push ['Time', header[1], 'Mean']

    iteration = 1
    row_id = 0
    prev_id = 0
    max_iteration_total = 0
    max_iteration_count = 1
    CSV.foreach(@csv, {:col_sep => ' '}) do |row|
      # Reset when we exceed the reinterval
      if (row_id / iteration) > @columns[index].reinterval
        # puts "Processing iteration #{iteration}"
        retval.push [prev_id, max_iteration_total / max_iteration_count, @columns[index].mean]
        max_iteration_total = row[index].to_f
        max_iteration_count = 1
        iteration += 1
      end
      
      max_iteration_count += 1
      max_iteration_total += row[index].to_f

      if row_id == 0
        retval.push [row_id, row[index].to_f, @columns[index].mean]
      end
      
    end

    prev_id = row_id
    row_id += @interval

    # puts "Processing iteration #{iteration}"
    if max_iteration_count > 1
      retval.push [prev_id, max_iteration_total / max_iteration_count, @columns[index].mean] 
    else
      retval.push [row_id, row[index].to_f, @columns[index].mean] 
    end

    return retval
  end

  def get_column_sample_max(name)
    header = header_from(name)
    index  = header[0]
    retval = []
    prev = [ 0, 0, 0 ]

    retval.push ['Time', header[1], 'Mean']

    iteration = 1
    row_id = 0
    prev_id = 0
    max_iteration_total = 0
    max_iteration_count = 1
    CSV.foreach(@csv, {:col_sep => ' '}) do |row|
      prev.push row[index].to_i 
      prev.shift
      
      if is_peak(prev) || row_id == 0
        
        # Reset when we exceed the reinterval
        if (row_id / iteration) > @columns[index].reinterval
          # puts "Processing iteration #{iteration}"
          retval.push [prev_id, max_iteration_total / max_iteration_count, @columns[index].mean]
          max_iteration_total = row[index].to_f
          max_iteration_count = 1
          iteration += 1
        end

        max_iteration_count += 1
        max_iteration_total += row[index].to_f
      else
        if row_id == 0
          retval.push [row_id, row[index].to_f, @columns[index].mean]
        end

      end
      prev_id = row_id
      row_id += @interval
    end
    # puts "Processing iteration #{iteration}"
    if max_iteration_count > 1
      retval.push [prev_id, max_iteration_total / max_iteration_count, @columns[index].mean] 
    else
      retval.push [row_id, row[index].to_f, @columns[index].mean] 
    end

    return retval
  end

  def get_column_sample_min(name)
    header = header_from(name)
    index  = header[0]
    retval = []
    prev = [ 0, 0, 0 ]

    retval.push ['Time', header[1], 'Mean']


    iteration = 1
    row_id = 0
    prev_id = 0
    min_iteration_total = 0
    min_iteration_count = 1
    CSV.foreach(@csv, {:col_sep => ' '}) do |row|
      prev.push row[index].to_f
      prev.shift

      if is_peak2(prev, -1) 
      p prev
       
        # Reset when we exceed the reinterval
        if (row_id / iteration) > @columns[index].reinterval
          # puts "Processing iteration #{iteration}"
          retval.push [prev_id, min_iteration_total / min_iteration_count, @columns[index].mean]
          min_iteration_total = row[index].to_f
          min_iteration_count = 1
          iteration += 1
        end

        min_iteration_count += 1
        min_iteration_total += row[index].to_f
      else
        if row_id == 0
          retval.push [row_id, row[index].to_f, @columns[index].mean]
        end
      end
      prev_id = row_id
      row_id += @interval
    end

    # puts "Processing iteration #{iteration}"
    if min_iteration_count > 1
      retval.push [prev_id, min_iteration_total / min_iteration_count, @columns[index].mean] 
    else 
      retval.push [row_id, prev[2], @columns[index].mean] 
    end
    return retval
  end

  def parse    
    # First pass
    CSV.foreach(@csv, {:col_sep => ' '}) do |row|
      col_count = row.length

      while i = row.shift do
        update_column_metadata(col_count - row.length - 1, i.to_f)
      end
    end
  end
end

options = {}
OptionParser.new do |opts|
  opts.banner = "Usage: jstatd_results.rb -i SECONDS jstatfile.csv" 

  opts.on("-i", "--interval SECONDS",
          "Interval in SECONDS between rows") do |i|
    options[:interval] = i.to_i
  end
  
  opts.on("-v", "--[no-]verbose", "Run verbosely") do |v|
    options[:verbose] = v
  end
end.parse!

# pp optparse.parse!(ARGV)
  
ARGV.each do|f|
  parser = JStatdParser.new(f, options[:interval])
  writer = JStatdRewriter.new(parser, { :minmax   => true,
                                        :perm_gen => true,
                                        :old_gen  => true,
                                        :fgct     => true,
                                        :gct      => true })
  
  sleep 0.5
end


