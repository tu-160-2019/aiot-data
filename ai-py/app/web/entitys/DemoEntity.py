from app import db
from datetime import datetime

class DemoEntity(db.Model):
    __tablename__ = 'SYS_CONFIG'
    config_id = db.Column(db.Integer, primary_key=True)
    config_name = db.Column(db.String(100))
    config_key = db.Column(db.String(100))
    config_value = db.Column(db.String(500))
    config_type = db.Column(db.Integer)
    create_by = db.Column(db.String(64))
    create_time = db.Column(db.DATETIME, default=datetime.now)
    update_by = db.Column(db.String(64))
    update_time = db.Column(db.DATETIME, default=datetime.now)
    remark = db.Column(db.String(500))

    def to_json(self):
        return {
            'configId': self.config_id,
            'configName': self.config_name,
            'configKey': self.config_key,
            'configValue': self.config_value,
            'configType': self.config_type,
            'createBy': self.create_by,
            'createTime': self.create_time,
            'updateBy': self.update_by,
            'updateTime': self.update_time,
            'remark': self.remark,
        }

    def __repr__(self):
        return '<Config %r>\n' %(self.config_name)