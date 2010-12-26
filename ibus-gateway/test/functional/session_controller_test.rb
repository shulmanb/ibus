require 'test_helper'

class SessionControllerTest < ActionController::TestCase
  # Replace this with your real tests.

  test "should_route_to_create_session" do
    assert_routing({:path=>"/session", :method=>:post}, {:controller => "session", :action => "create_session"})
  end
  
  test "should_route_to_validate_session" do
    assert_routing({:path=>"/session/1", :method=>:get}, {:controller => "session", :action => "validate_session"})
  end

  test "create anonimous session" do
    post(:create_session, {'data'=>{'user'=>'test'}},nil)
    print @response.budy   
  end

end
