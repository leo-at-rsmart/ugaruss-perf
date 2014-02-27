#!/usr/bin/env ruby

#
# == Description
#
# Simple authentication test that logs in and back out
#
# === Issues
#
# Jira 1234 - Fake jira issue

require 'drb'

# Test info - default test case setup
test = File.basename(__FILE__)
tconfig = DRbObject.new nil, "druby://localhost:#{ENV['DRB_PORT']}"
require tconfig.lib_base_dir + '/../lib/kfs-api.rb'
require tconfig.lib_base_dir + "/#{tconfig.product}/common/authentication.rb"
require tconfig.lib_base_dir + "/#{tconfig.product}/dv/dv.rb"
probability = tconfig.tests[test]
tconfig.log.info_msg("Test: #{test}")
tconfig.log.info_msg("Probability: #{tconfig.tests[test]}")

# Create session, transation and request container objects
sesh = Session.new(tconfig, 'basic_auth', probability)
li_txn = sesh.add_transaction("login")
li_req = li_txn.add_requests

tconfig.log.info_msg("#{test}: Logging in as lep12004")
auth = Authentication.new(li_req)
auth.login
li_req.add_thinktime(30)

tconfig.log.info_msg("#{test}: Creating a disbursement voucher")
dv_txn = sesh.add_transaction("disbursement_voucher")
dv_req = dv_txn.add_requests
dv = DisbursementVoucher.new(dv_req)
dv.create
dv.copy('10111')

# Logout
tconfig.log.info_msg("#{test}: Logging out")
lo_txn = sesh.add_transaction("logout")
lo_req = lo_txn.add_requests
auth2 = Authentication.new(lo_req)
auth2.logout


