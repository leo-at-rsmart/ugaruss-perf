#!/usr/bin/env ruby

# 
# == Synopsis
#
# Authentication class containing all operations around app authentication
#
# Author:: Leo Przybylski (mailto:leo@rsmart.com)

#require File.dirname(__FILE__) + '/../organization/organization.rb'
require 'uri'
require 'drb' 

class Authentication
  
  attr_accessor :request
  
  def initialize(request_obj)
    @request = request_obj
  end
  
  # Login to ks
  def login(opts={})
    
    defaults = {
      :user                 => 'admin',
      :password             => 'admin',
      :thinktime            => 3,
      :redir_dyn_var_name   => 'redir',
      :redir_dyn_var_regex  => 'Location: (.*)',
      :lt_dyn_var_name      => 'lt',
      :lt_dyn_var_xpath     => "//input[@name='lt']/@value",
      :ticket_dyn_var_name  => 'ticket',
      :ticket_dyn_var_regex => 'Location: .*ticket=(.*)',
      :execution_dyn_var_name  => 'execution',
      :execution_dyn_var_xpath => "//input[@name='execution']/@value",
      :cas_url              => 'https://login.uconn.edu/cas'
    }
    
    tconfig = DRbObject.new nil, "druby://localhost:#{ENV['DRB_PORT']}"
    opts = defaults.merge(opts)
    
    tconfig.log.info_msg("Using portal url #{@request.url}")
    @request.add('/portal.do', 
                 {
                   'method' => 'GET'
                 },
                 {
                   :dyn_variables => [
                                      { "name" => opts[:redir_dyn_var_name], "re" => opts[:redir_dyn_var_regex] }
                                     ]
                 }
                 )    
    @request.add("%%_redir%%",
                 {
                   'method' => 'GET'
                 },
                 {
                   :dyn_variables => [
                                      { "name" => opts[:lt_dyn_var_name],        "xpath" => opts[:lt_dyn_var_xpath] },
                                      { "name" => opts[:execution_dyn_var_name], "xpath" => opts[:execution_dyn_var_xpath] }
                                     ],
                   'subst' => 'true'
                 }
                 )

    kfs_url_escaped = URI.escape("#{@request.url}/portal.do", Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
    @request.add("#{opts[:cas_url]}/login?service=#{kfs_url_escaped}",
                 {
                   'method'       => 'POST',
                   'content_type' => 'application/x-www-form-urlencoded',
                   'contents'     => "lt=%%_#{opts[:lt_dyn_var_name]}%%&amp;execution=%%_#{opts[:execution_dyn_var_name]}%%&amp;username=#{opts[:user]}&amp;password=#{opts[:password]}&amp;_eventId=submit"
                 },
                 {
                   :dyn_variables => [
                                      { "name" => opts[:ticket_dyn_var_name], "re" => opts[:ticket_dyn_var_xpath] }
                                     ],
                   'subst' => 'true'
                 }
                 )
    
    @request.add("#{@request.url}/portal.do?ticket=%%_ticket%%",
                 {
                   'method' => 'GET'
                 },
                 {
                   :dyn_variables => [
                                      { "name" => opts[:redir_dyn_var_name], "re" => opts[:redir_dyn_var_regex] }
                                     ],
                   'subst' => 'true'
                 }               
                 )
    @request.add("%%_redir%%")
  end
 

  # Logout of KFS (Not all versions support this)
  def logout
      kfs_url_escaped = URI.escape("#{@request.url}/portal.do", Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
      @request.add("https://login.uconn.edu/cas/login?service=#{kfs_url_escaped}")
  end

  def backdoor(user)
    backdoor_url = URI.escape("#{@request.url}/backdoorlogin.do", Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
    @request.add(backdoor_url,
                 {
                   'method' => 'POST',
                   'content_type' => 'application/x-www-form-urlencoded',
                   'contents' => "methodToCall=login&amp;backdoorId=${user}"
                 }
                 )
    
  end
  
  # Bring up homepage
  def load_homepage
    
  end
  
end
