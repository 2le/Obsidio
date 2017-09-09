package com.benberi.cadesim.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.RandomUtils;

import java.util.Random;

public class ConnectScene implements GameScene, InputProcessor {

    private GameContext context;

    private ConnectionSceneState state = ConnectionSceneState.DEFAULT;

    /**
     * Batch for opening screen
     */
    private SpriteBatch batch;

    /**
     * The shape renderer
     */
    private ShapeRenderer renderer;

    /**
     * Background texture
     */
    private Texture background;

    private int connectAnimationState = 0;

    private long lastConnectionAnimatinoStateChange;

    private BitmapFont font;

    private boolean failed;

    /**
     * The main stage for elements
     */
    private Stage stage;

    /**
     * The username textfield
     */
    private TextField name;

    /**
     * The address textfield
     */
    private TextField address;

    /**
     * Username textfield texture
     */
    private Texture nameTexture;

    /**
     * The address textfield texture
     */
    private Texture addressTexture;

    /**
     * The login button texture
     */
    private Texture loginButton;

    private Texture loginButtonHover;

    /**
     * If a popup is open
     */
    private boolean popup;

    private boolean loggingIn;


    /**
     * The popup message
     */
    private String popupMessage;

    private boolean popupCloseHover;
    private boolean loginHover;
    private boolean validating;

    public ConnectScene(GameContext ctx) {
        this.context = ctx;
    }

    @Override
    public void create() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/FjallaOne-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.shadowColor = new Color(0, 0, 0, 0.8f);
        parameter.shadowOffsetY = 1;
        font = generator.generateFont(parameter);
        font.setColor(Color.YELLOW);
        batch = new SpriteBatch();

        background = new Texture("core/assets/bg.png");
        nameTexture = new Texture("core/assets/skin/textfield-name.png");
        addressTexture = new Texture("core/assets/skin/textfield-address.png");
        loginButton = new Texture("core/assets/skin/login.png");
        loginButtonHover = new Texture("core/assets/skin/login-hover.png");

        renderer = new ShapeRenderer();

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        setup();

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = new Color(0.16f, 0.16f, 0.16f, 1);
        style.cursor = new Image(new Texture("core/assets/skin/textfield-cursor.png")).getDrawable();
        style.selection = new Image(new Texture("core/assets/skin/textfield-selection.png")).getDrawable();

        name = new TextField("", style);
        name.setSize(160, 49);
        name.setPosition(170, 225);

        address = new TextField("", style);
        address.setSize(225, 49);
        address.setPosition(370, 225);

        stage.addActor(name);
        stage.addActor(address);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(background, 0, 0);

        if (state == ConnectionSceneState.DEFAULT) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Display name:", 160, 300);
            batch.draw(nameTexture, 160, 225);
            font.draw(batch, "IP Address:", 360, 300);
            batch.draw(addressTexture, 360, 225);

            if (!loginHover) {
                batch.draw(loginButton, 165, 170);
            } else {
                batch.draw(loginButtonHover, 165, 170);
            }
            font.setColor(new Color(0.1f, 0.1f, 0.1f, 1));


            font.draw(batch, "Connect", 340, 196);

            batch.end();

            stage.act();
            stage.draw();

            if (popup) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(new Color(0f, 0f, 0f, 0.9f));
                renderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                GlyphLayout layout = new GlyphLayout(font, popupMessage);


                int x = Gdx.graphics.getWidth() / 2 - 200;
                int y = Gdx.graphics.getHeight() / 2 - 50;
                int width = 400;
                int height = 50;

                renderer.setColor(new Color(213 / 255f, 54 / 255f, 53 / 255f, 1));
                renderer.rect(x, y, width, height);

                if (popupCloseHover) {
                    renderer.setColor(new Color(250 / 255f, 93 / 255f, 93 / 255f, 1));
                } else {
                    renderer.setColor(new Color(170 / 255f, 39 / 255f, 39 / 255f, 1));
                }
                renderer.rect(x + 330, y, 70, 50);
                renderer.end();
                batch.begin();
                font.setColor(Color.WHITE);
                font.draw(batch, popupMessage, x + ((400 / 2) - layout.width / 2) - 30, y + (25 + (layout.height / 2)));

                font.draw(batch, "Close", x + 400 - 55, y + (25 + (layout.height / 2)));
                batch.end();
            }
        }
        else {

			/*
			 * Cheap way of animation dots lol...
			 */
            String dot = "";

            if (connectAnimationState == 0) {
                dot = ".";
            }
            else if (connectAnimationState == 1) {
                dot = "..";
            }
            else if (connectAnimationState == 2) {
                dot = "...";
            }

                font.setColor(Color.YELLOW);
                String text = "";

                if (state == ConnectionSceneState.CREATING_PROFILE) {
                    text = "Creating profile";
                }
                else if (state == ConnectionSceneState.CONNECTING) {
                    text = "Connecting, please wait";
                }
                else if (state == ConnectionSceneState.CREATING_MAP) {
                    text = "Waiting for board map update";
                }

                GlyphLayout layout = new GlyphLayout(font, text);
                font.draw(batch, text + dot, Gdx.graphics.getWidth() / 2 - (layout.width / 2), 300);

            if (System.currentTimeMillis() - lastConnectionAnimatinoStateChange >= 200) {
                connectAnimationState++;
                lastConnectionAnimatinoStateChange = System.currentTimeMillis();
            }
            if(connectAnimationState > 2) {
                connectAnimationState = 0;
            }
            batch.end();
        }
    }


    public void setPopup(String message) {
        popup = true;
        popupMessage = message;
        name.setDisabled(true);
        address.setDisabled(true);
    }

    public void closePopup() {
        popup = false;
        name.setDisabled(false);
        address.setDisabled(false);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean handleDrag(float screenX, float screenY, float diffX, float diffY) {
        return false;
    }

    @Override
    public boolean handleClick(float x, float y, int button) {
        return false;
    }

    @Override
    public boolean handleClickRelease(float x, float y, int button) {
        return false;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.CENTER) {
            if (!popup) {
                if (stage.getKeyboardFocus() != name && name.getText().isEmpty()) {
                    stage.setKeyboardFocus(name);
                } else if (stage.getKeyboardFocus() != address && address.getText().isEmpty()) {
                    stage.setKeyboardFocus(address);
                } else {
                    performLogin();
                }
            }
            else {
                closePopup();
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (popup && popupCloseHover) {
            closePopup();
        }
        else if (loginHover) {
            performLogin();
        }
        return false;
    }

    private void performLogin() {
        if (name.getText().length() >= 20) {
            setPopup("Display name must be shorter.");
        }
        else if (name.getText().length() <= 0) {
            setPopup("Please enter a display name");
        }
        else if (address.getText().length() <= 0) {
            setPopup("Please enter an IP Address");
        }
        else if (!RandomUtils.validIP(address.getText())) {
            setPopup("Please enter a valid IP Address");
        }
        else {
            setState(ConnectionSceneState.CONNECTING);
            context.connect(name.getText(), address.getText());
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (popup) {
            loginHover = false;
           // 505 398
            popupCloseHover = screenX >= 505 && screenX <= 573 && screenY >= 349 && screenY <= 398;
        }
        else {
            loginHover = screenX >= 164 && screenY <= 606 && screenY >= 490 && screenY <= 530;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void loginFailed() {
        loggingIn = false;
        setPopup("Could not connect to server.");
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
        this.loggingIn = validating;
    }

    public void setState(ConnectionSceneState state) {
        this.state = state;
    }

    public void setup() {
        setState(ConnectionSceneState.DEFAULT);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }
}
