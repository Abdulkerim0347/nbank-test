package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object update(int id, BaseModel model);
    Object get(int id);
    Object delete(int id);
}
