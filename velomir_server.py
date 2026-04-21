# velomir_server.py
from flask import Flask, jsonify, request
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
import uuid
import os

app = Flask(__name__)
CORS(app)

# Настройка базы данных SQLite (файл velomir.db появится в папке сервера)
basedir = os.path.abspath(os.path.dirname(__file__))
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, 'velomir.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# --- Модели данных (аналогичны Kotlin-классам) ---

class BikeType(db.Model):
    __tablename__ = 'bike_types'
    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(100), nullable=False)
    # Связь с производителями (один ко многим)
    manufacturers = db.relationship('Manufacturer', backref='bike_type', lazy=True, cascade="all, delete-orphan")

    def to_dict(self):
        return {'id': self.id, 'name': self.name}

class Manufacturer(db.Model):
    __tablename__ = 'manufacturers'
    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(100), nullable=False)
    bike_type_id = db.Column(db.String(36), db.ForeignKey('bike_types.id'), nullable=False)
    # Связь с моделями
    models = db.relationship('BikeModel', backref='manufacturer', lazy=True, cascade="all, delete-orphan")

    def to_dict(self):
        return {'id': self.id, 'name': self.name, 'bikeTypeID': self.bike_type_id}

class BikeModel(db.Model):
    __tablename__ = 'bike_models'
    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(100), nullable=False)
    phone = db.Column(db.String(20), nullable=True)
    manufacturer_id = db.Column(db.String(36), db.ForeignKey('manufacturers.id'), nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'phone': self.phone,
            'manufacturerID': self.manufacturer_id
        }

# --- Создание таблиц (выполняется один раз при старте) ---
with app.app_context():
    db.create_all()

# --- API эндпоинты ---

@app.route('/bike-types', methods=['GET'])
def get_bike_types():
    types = BikeType.query.all()
    return jsonify([t.to_dict() for t in types])

@app.route('/bike-types', methods=['POST'])
def add_bike_type():
    data = request.json
    new_type = BikeType(name=data['name'])
    db.session.add(new_type)
    db.session.commit()
    return jsonify(new_type.to_dict()), 201

@app.route('/bike-types/<type_id>', methods=['PUT'])
def update_bike_type(type_id):
    bike_type = BikeType.query.get_or_404(type_id)
    data = request.json
    bike_type.name = data['name']
    db.session.commit()
    return jsonify(bike_type.to_dict())

@app.route('/bike-types/<type_id>', methods=['DELETE'])
def delete_bike_type(type_id):
    bike_type = BikeType.query.get_or_404(type_id)
    db.session.delete(bike_type)
    db.session.commit()
    return '', 204

# --- Производители (только для конкретного типа) ---
@app.route('/bike-types/<type_id>/manufacturers', methods=['GET'])
def get_manufacturers_by_type(type_id):
    manufacturers = Manufacturer.query.filter_by(bike_type_id=type_id).all()
    return jsonify([m.to_dict() for m in manufacturers])

@app.route('/manufacturers', methods=['POST'])
def add_manufacturer():
    data = request.json
    new_man = Manufacturer(name=data['name'], bike_type_id=data['bikeTypeID'])
    db.session.add(new_man)
    db.session.commit()
    return jsonify(new_man.to_dict()), 201

@app.route('/manufacturers/<man_id>', methods=['PUT'])
def update_manufacturer(man_id):
    man = Manufacturer.query.get_or_404(man_id)
    data = request.json
    man.name = data['name']
    db.session.commit()
    return jsonify(man.to_dict())

@app.route('/manufacturers/<man_id>', methods=['DELETE'])
def delete_manufacturer(man_id):
    man = Manufacturer.query.get_or_404(man_id)
    db.session.delete(man)
    db.session.commit()
    return '', 204

# --- Модели (для конкретного производителя) ---
@app.route('/manufacturers/<man_id>/models', methods=['GET'])
def get_models_by_manufacturer(man_id):
    models = BikeModel.query.filter_by(manufacturer_id=man_id).all()
    return jsonify([m.to_dict() for m in models])

@app.route('/models', methods=['POST'])
def add_model():
    data = request.json
    new_model = BikeModel(
        name=data['name'],
        phone=data.get('phone', ''),
        manufacturer_id=data['manufacturerID']
    )
    db.session.add(new_model)
    db.session.commit()
    return jsonify(new_model.to_dict()), 201

@app.route('/models/<model_id>', methods=['PUT'])
def update_model(model_id):
    model = BikeModel.query.get_or_404(model_id)
    data = request.json
    model.name = data['name']
    model.phone = data.get('phone', '')
    db.session.commit()
    return jsonify(model.to_dict())

@app.route('/models/<model_id>', methods=['DELETE'])
def delete_model(model_id):
    model = BikeModel.query.get_or_404(model_id)
    db.session.delete(model)
    db.session.commit()
    return '', 204

# --- Синхронизация (полное обновление всех данных) ---
@app.route('/sync/all', methods=['POST'])
def sync_all():
    """
    Принимает JSON со всеми типами, производителями и моделями.
    Полностью заменяет содержимое базы данных.
    """
    data = request.json
    # Удаляем все существующие записи (каскадно удалятся производители и модели)
    BikeType.query.delete()
    for type_data in data.get('bikeTypes', []):
        bike_type = BikeType(id=type_data['id'], name=type_data['name'])
        db.session.add(bike_type)
    for man_data in data.get('manufacturers', []):
        man = Manufacturer(id=man_data['id'], name=man_data['name'], bike_type_id=man_data['bikeTypeID'])
        db.session.add(man)
    for model_data in data.get('bikeModels', []):
        model = BikeModel(
            id=model_data['id'],
            name=model_data['name'],
            phone=model_data.get('phone', ''),
            manufacturer_id=model_data['manufacturerID']
        )
        db.session.add(model)
    db.session.commit()
    return jsonify({'success': True, 'message': 'Данные синхронизированы'})

if __name__ == '__main__':
    print("Сервер веломира запущен:")
    print("  Локально: http://127.0.0.1:5000")
    print("  Для эмулятора Android: http://10.0.2.2:5000")
    app.run(debug=True, host='0.0.0.0', port=5000)
