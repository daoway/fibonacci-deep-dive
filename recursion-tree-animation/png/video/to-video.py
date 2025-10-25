import cv2
import os

IMAGE_FOLDER = '.' 
VIDEO_NAME = 'output_video.mp4'
SECONDS_PER_FRAME = 5
FPS = 1.0 / SECONDS_PER_FRAME 

def create_video_from_frames(image_folder, video_name, fps):
    try:
        images = [img for img in os.listdir(image_folder) if img.endswith((".png", ".jpg", ".jpeg"))]
        images.sort()
    except FileNotFoundError:
        print(f"Помилка: Папку не знайдено за шляхом: {image_folder}")
        return

    if not images:
        print(f"Помилка: У папці {image_folder} не знайдено зображень.")
        return

    first_frame_path = os.path.join(image_folder, images[0])
    frame = cv2.imread(first_frame_path)
    if frame is None:
        print(f"Помилка: Не вдалося прочитати перший кадр: {first_frame_path}")
        return

    height, width, layers = frame.shape
    size = (width, height)

    fourcc = cv2.VideoWriter_fourcc(*'mp4v') # Кодек для MP4
    video = cv2.VideoWriter(video_name, fourcc, fps, size)

    print(f"Створення відео: {video_name}")
    print(f"Кількість кадрів: {len(images)}")
    print(f"Розмір кадру: {width}x{height}")
    print(f"Частота кадрів: {fps} FPS (затримка 10 сек/кадр)")

    # 4. Запис кадрів у відео
    for i, image_file in enumerate(images):
        full_path = os.path.join(image_folder, image_file)
        current_frame = cv2.imread(full_path)
        
        if current_frame is not None and current_frame.shape[:2] == (height, width):
            video.write(current_frame)
            print(f"  > Додано кадр {i+1}/{len(images)}: {image_file}")
        else:
            print(f"  !!! Помилка: Пропущено кадр {image_file} (не вдалося прочитати або розмір не відповідає).")

    
    video.release()
    cv2.destroyAllWindows()
    print("-" * 30)
    print(f"Успіх! Відео збережено як: {video_name}")

if __name__ == '__main__':
    create_video_from_frames(IMAGE_FOLDER, VIDEO_NAME, FPS)
