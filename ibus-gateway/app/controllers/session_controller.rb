class SessionController < ApplicationController
  
  def create_session
    user = params[:data][:user]
    psswd = params[:data][:psswd]
    @s = Session.new
    @s.valid = true
    if psswd == nil
      @s.id = @@wrapper.createAnonimousSession user
    else
      @s.id = @@wrapper.createSession user, psswd
    end  
    format @s
  end

  def validate_session
    session = params[:session]
    @s = Session.new
    @s.id = session
    @s.valid = @@wrapper.validateSession session
    format @s
  end
end
