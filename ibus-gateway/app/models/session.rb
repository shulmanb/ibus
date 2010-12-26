class Session
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml

  attr_accessor :id 
  attr_accessor :valid
  def attributes
    @attributes ||= {'id' => nil,'valid'=>true}
  end
  
 end