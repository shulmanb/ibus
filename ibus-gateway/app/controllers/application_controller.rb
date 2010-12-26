require 'Wrapper'

class ApplicationController < ActionController::Base
  #protect_from_forgery

  @@wrapper = Wrapper.new
  def format(out)
    respond_to do |format|
      format.html { render :json => out }
      format.xml { render :xml => out }
      format.json { render :json => out }
    end
  end

  def format_empty(status)
    respond_to do |format|
          format.html { render :text => '', :status => status}
          format.xml { render :text => '', :status => status }
          format.json { render :text => '', :status => status }
        end  end
end
