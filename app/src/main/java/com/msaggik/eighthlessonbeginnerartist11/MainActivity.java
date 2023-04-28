package com.msaggik.eighthlessonbeginnerartist11;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // создание полей
    private PaintingView paintingView; // создание поля кастомизированного View
    private ImageButton fabPalette; // поле кнопки палитры
    private ImageButton fabBroom; // поле кнопки нового рисунка
    private ImageButton fabSave; // поле кнопки сохранения рисунка
    private Bitmap bitmapPalette; // поле растрового изображения палитры

    private boolean isEraser = false; // задание поля кисти


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // присваивание полям id
        paintingView = findViewById(R.id.painting); // присваивание полю View
        fabPalette = findViewById(R.id.fabPalette); // присваивание полю разметки кнопки fabPalette
        fabBroom = findViewById(R.id.fabBroom); // присваивание полю разметки кнопки fabBroom
        fabSave = findViewById(R.id.fabSave); // присваивание полю разметки кнопки fabSave

        // обработка нажатия кнопок
        fabBroom.setOnClickListener(listener);
        fabPalette.setOnClickListener(listener);
        fabSave.setOnClickListener(listener);

    }

    // создание слушателя
    private View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onClick(View view) {
            // исполняемый код при нажатии кнопок
            switch (view.getId()) {
                case R.id.fabPalette:
                    // код для задания палитры
                    Dialog paletteDialog = new Dialog(MainActivity.this, R.style.Dialog); // создадим диалоговое окно палитры
                    paletteDialog.setTitle("Палитра:"); // зададим заголовок диалогового окна
                    paletteDialog.setContentView(R.layout.activity_palette); // добавим разметку диалогового окна

                    ImageView colorSelection = paletteDialog.findViewById(R.id.colorSelection); // создание поля картинки палитры и привязка к разметке по id
                    View colorInfoView = paletteDialog.findViewById(R.id.colorInfoView); // создание поля View для выбранного цвета и привязка к разметке по id

                    colorSelection.setDrawingCacheEnabled(true); // включение задания кэша картинки
                    colorSelection.buildDrawingCache(true); // включение постройки кэша картинки

                    // задание слушателя обработки касания картинки
                    colorSelection.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            // если пользователь коснулся экрана или поводил по нему пальцем, то
                            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                                bitmapPalette = colorSelection.getDrawingCache(); // запись картинки в растровое изображение

                                int pixel = bitmapPalette.getPixel((int)motionEvent.getX(),(int)motionEvent.getY()); // определение цвета выбранного пикселя
                                String pixelHex = Integer.toHexString(pixel); // конвертирование данных о пикселе в шестнадцатиричный формат HEX
                                // считывание отдельных цветовых параметров пикселя
                                int a = Color.alpha(pixel);
                                int r = Color.red(pixel);
                                int g = Color.green(pixel);
                                int b = Color.blue(pixel);

                                // задание цвета фона для View выбора цвета
                                colorInfoView.setBackgroundColor(Color.argb(a, r, g, b));

                                // задание нового цвета для кисти
                                paintingView.setColor(pixel);
                            }
                            return true;
                        }
                    });

                    @SuppressLint("UseSwitchCompatOrMaterialCode")
                    Switch eraserSwitch = paletteDialog.findViewById(R.id.eraserChecked); // создание поля и привязка к нему id разметки ластика
                    eraserSwitch.setChecked(isEraser); // задание положения тумблера
                    eraserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            isEraser = b; // присваивание поля выбора ластика изменённому значению тумблера
                            paintingView.setBrush(!b); // задание выбора ластика или кисти
                        }
                    });

                    RadioGroup size = paletteDialog.findViewById(R.id.size); // создание поля и привязка к нему id разметки размеров кисти и ластика
                    size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (i) {
                                case R.id.radioSize15:
                                    paintingView.setSize(15); // задание выбора размера кисти и ластика
                                    break;
                                case R.id.radioSize30:
                                    paintingView.setSize(30); // задание выбора размера кисти и ластика
                                    break;
                                case R.id.radioSize50:
                                    paintingView.setSize(50); // задание выбора размера кисти и ластика
                                    break;
                                case R.id.radioSize70:
                                    paintingView.setSize(70); // задание выбора размера кисти и ластика
                                    break;
                                case R.id.radioSize90:
                                    paintingView.setSize(90); // задание выбора размера кисти и ластика
                                    break;
                            }
                        }
                    });
                    // обработка сохранения внесённых изменений
                    Button btnYes = paletteDialog.findViewById(R.id.btnYes);
                    Button btnNo = paletteDialog.findViewById(R.id.btnNo);
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewYes) {
                            paintingView.invalidate(); // обновление View
                            paletteDialog.dismiss(); // закрытие диалога
                        }
                    });
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewNo) {
                            paintingView.invalidate(); // обновление View
                            paletteDialog.cancel(); // выход из диалога
                        }
                    });

                    paletteDialog.show(); // показ диалогового окна
                    break;
                case R.id.fabBroom:
                    // код для создания нового рисунка
                    AlertDialog.Builder broomDialog = new AlertDialog.Builder(MainActivity.this); // создание диалогового окна типа AlertDialog
                    broomDialog.setTitle("Новый рисунок"); // заголовок диалогового окна
                    broomDialog.setMessage("Создать новый рисунок (имеющийся будет удалён)?"); // сообщение диалога

                    broomDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){ // пункт выбора "да"
                        public void onClick(DialogInterface dialog, int which){
                            paintingView.broom(); // запуск нового рисунка
                            dialog.dismiss(); // закрыть диалог
                        }
                    });
                    broomDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){  // пункт выбора "нет"
                        public void onClick(DialogInterface dialog, int which){
                            dialog.cancel(); // выход из диалога
                        }
                    });
                    broomDialog.show(); // отображение на экране данного диалога
                    break;
                case R.id.fabSave:
                    // код для сохранения документа
                    AlertDialog.Builder saveDialog = new AlertDialog.Builder(MainActivity.this); // создание диалогового окна типа AlertDialog
                    saveDialog.setTitle("Сохранить"); // заголовок диалогового окна
                    saveDialog.setMessage("Сохранить рисунок?"); // сообщение диалога
                    saveDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){ // пункт выбора "да"
                        public void onClick(DialogInterface dialog, int which){

                            // код сохранения рисунка
                            paintingView.setDrawingCacheEnabled(true); // сохраним кэш имеющегося рисунка

                            // сохранение изображения в файл
                            // (метод insertImage() записывает изображение в постоянную память устройства,
                            // SystemClock.uptimeMillis() - генерирует название файла в виде времени его создавания)
                            String imageSaved = MediaStore.Images.Media.insertImage(
                                    getContentResolver(), paintingView.getDrawingCache(),
                                    SystemClock.uptimeMillis() +".png", "");

                            // вывод тоста информации о сохранении рисунка
                            if(imageSaved != null) { // если изображение сохранено, то вывод тоста об успешности сохранения
                                Toast.makeText(getApplicationContext(),
                                        "Изображение успешно сохранено в галлерею!", Toast.LENGTH_SHORT).show();
                            } else { // иначе, вывод тоста об неудачном сохранении
                                Toast.makeText(getApplicationContext(),
                                        "Сохранить изображение не удалось!", Toast.LENGTH_SHORT).show();
                            }
                            paintingView.destroyDrawingCache(); // удаление кэша рисунка
                        }
                    });
                    saveDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){ // пункт выбора "нет"
                            dialog.cancel(); // выход из диалога
                        }
                    });
                    saveDialog.show(); // отображение на экране данного диалога
                    break;
            }
        }
    };
}